package org.jenkinsci.plugins.sonargerrit.gerrit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.NotifyHandling;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.CommentInfo;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Resolves the unresolved comment threads opened by this plugin whose issue is no longer reported.
 *
 * @author Réda Housni Alaoui
 */
@Restricted(NoExternalUse.class)
public class StaleCommentResolver {

  static final String RESOLUTION_MESSAGE = "No longer reported by SonarQube.";

  private final int ownAccountId;
  private final int reviewedPatchSet;
  private final Map<String, List<CommentInfo>> publishedCommentsByPath;

  public StaleCommentResolver(
      int ownAccountId,
      int reviewedPatchSet,
      Map<String, List<CommentInfo>> publishedCommentsByPath) {
    this.ownAccountId = ownAccountId;
    this.reviewedPatchSet = reviewedPatchSet;
    this.publishedCommentsByPath = publishedCommentsByPath;
  }

  /**
   * Amends {@code review}, the review of the reviewed patch set, and adds a review for each older
   * patch set holding a stale thread. Gerrit requires a reply to be posted on the patch set of the
   * comment it replies to.
   *
   * @return The reviews to post, by patch set number
   */
  public Map<Integer, ReviewInput> amendReviews(ReviewInput review) {
    Map<Integer, List<CommentThread>> threadsByPatchSet =
        selectUnresolvedOwnThreads().stream()
            .filter(thread -> thread.patchSet() <= reviewedPatchSet)
            .collect(Collectors.groupingBy(CommentThread::patchSet));
    return Stream.concat(Stream.of(reviewedPatchSet), threadsByPatchSet.keySet().stream())
        .distinct()
        .collect(
            Collectors.toUnmodifiableMap(
                Function.identity(),
                patchSet ->
                    amend(
                        selectOrCreateReview(patchSet, review),
                        threadsByPatchSet.getOrDefault(patchSet, List.of()))));
  }

  /**
   * Drops from {@code review} the comments already opened as one of the unresolved {@code threads},
   * and adds to it the replies resolving the other threads.
   */
  private static ReviewInput amend(ReviewInput review, List<CommentThread> threads) {
    // Gerrit's duplicate detection also matches resolved comments. It would silently drop an issue
    // reported again after its thread was resolved.
    review.omitDuplicateComments = false;

    List<PathComment> newComments =
        Optional.ofNullable(review.comments).orElseGet(Map::of).entrySet().stream()
            .flatMap(PathComment::streamOf)
            .toList();
    Stream<PathComment> commentsToPost =
        newComments.stream().filter(newComment -> isNotYetOpened(newComment, threads));
    Stream<PathComment> resolvingReplies =
        threads.stream()
            .filter(thread -> isNoLongerReported(thread, newComments))
            .map(CommentThread::createResolvingReply);
    review.comments = groupByPath(Stream.concat(commentsToPost, resolvingReplies));
    return review;
  }

  private ReviewInput selectOrCreateReview(int patchSet, ReviewInput reviewedPatchSetReview) {
    if (patchSet == reviewedPatchSet) {
      return reviewedPatchSetReview;
    }
    ReviewInput review = new ReviewInput();
    review.tag = GerritReviewBuilder.REVIEW_TAG;
    review.notify = NotifyHandling.NONE;
    return review;
  }

  private List<CommentThread> selectUnresolvedOwnThreads() {
    List<CommentThread> threads = new ArrayList<>();
    for (Map.Entry<String, List<CommentInfo>> publishedCommentsByPathEntry :
        publishedCommentsByPath.entrySet()) {
      Map<String, CommentInfo> commentById =
          publishedCommentsByPathEntry.getValue().stream()
              .collect(Collectors.toMap(comment -> comment.id, Function.identity()));
      Map<CommentInfo, List<CommentInfo>> threadCommentsByRoot =
          publishedCommentsByPathEntry.getValue().stream()
              .collect(Collectors.groupingBy(comment -> findRoot(comment, commentById)));
      for (Map.Entry<CommentInfo, List<CommentInfo>> threadCommentsByRootEntry :
          threadCommentsByRoot.entrySet()) {
        CommentInfo root = threadCommentsByRootEntry.getKey();
        CommentInfo last =
            Collections.max(
                threadCommentsByRootEntry.getValue(),
                Comparator.comparing(comment -> comment.updated));
        if (isOwn(root) && Boolean.TRUE.equals(last.unresolved)) {
          threads.add(new CommentThread(publishedCommentsByPathEntry.getKey(), root, last));
        }
      }
    }
    return threads;
  }

  private static CommentInfo findRoot(CommentInfo comment, Map<String, CommentInfo> commentById) {
    CommentInfo root = comment;
    while (root.inReplyTo != null && commentById.containsKey(root.inReplyTo)) {
      root = commentById.get(root.inReplyTo);
    }
    return root;
  }

  private boolean isOwn(CommentInfo comment) {
    return comment.author != null
        && Objects.equals(comment.author._accountId, ownAccountId)
        && GerritReviewBuilder.REVIEW_TAG.equals(comment.tag);
  }

  private static boolean isNotYetOpened(PathComment newComment, List<CommentThread> threads) {
    return threads.stream().noneMatch(thread -> thread.isOpenedBy(newComment));
  }

  private static boolean isNoLongerReported(CommentThread thread, List<PathComment> newComments) {
    return newComments.stream().noneMatch(thread::isOpenedBy);
  }

  /** Gerrit stores a line 0 as a file comment, and trims the messages. */
  private static boolean matches(ReviewInput.CommentInput comment, CommentInfo threadRoot) {
    return normalizeLine(comment.line) == normalizeLine(threadRoot.line)
        && StringUtils.trimToEmpty(comment.message)
            .equals(StringUtils.trimToEmpty(threadRoot.message));
  }

  private static int normalizeLine(Integer line) {
    return Optional.ofNullable(line).orElse(0);
  }

  private static Map<String, List<ReviewInput.CommentInput>> groupByPath(
      Stream<PathComment> comments) {
    return comments.collect(
        Collectors.groupingBy(
            PathComment::path,
            Collectors.mapping(PathComment::comment, Collectors.toUnmodifiableList())));
  }

  private record PathComment(String path, ReviewInput.CommentInput comment) {

    static Stream<PathComment> streamOf(
        Map.Entry<String, List<ReviewInput.CommentInput>> commentsByPathEntry) {
      return commentsByPathEntry.getValue().stream()
          .map(comment -> new PathComment(commentsByPathEntry.getKey(), comment));
    }
  }

  private record CommentThread(String path, CommentInfo root, CommentInfo last) {

    int patchSet() {
      return root.patchSet;
    }

    boolean isOpenedBy(PathComment comment) {
      return path.equals(comment.path()) && matches(comment.comment(), root);
    }

    PathComment createResolvingReply() {
      ReviewInput.CommentInput reply = new ReviewInput.CommentInput();
      reply.inReplyTo = last.id;
      reply.side = root.side;
      reply.line = root.line;
      reply.range = root.range;
      reply.message = RESOLUTION_MESSAGE;
      reply.unresolved = false;
      return new PathComment(path, reply);
    }
  }
}
