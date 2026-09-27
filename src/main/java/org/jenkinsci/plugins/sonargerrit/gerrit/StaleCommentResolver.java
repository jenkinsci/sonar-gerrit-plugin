package org.jenkinsci.plugins.sonargerrit.gerrit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
   * Drops from {@code review} the comments already opened as an unresolved thread of the reviewed
   * patch set, and adds to it the replies resolving the other threads of that patch set.
   *
   * @return The reviews resolving the threads of older patch sets, by patch set number. Gerrit
   *     requires a reply to be posted on the patch set of the comment it replies to.
   */
  public Map<Integer, ReviewInput> resolveStaleThreads(ReviewInput review) {
    // Gerrit's duplicate detection also matches resolved comments. It would silently drop an issue
    // reported again after its thread was resolved.
    review.omitDuplicateComments = false;

    Map<String, List<ReviewInput.CommentInput>> comments = new HashMap<>();
    if (review.comments != null) {
      for (Map.Entry<String, List<ReviewInput.CommentInput>> pathComments :
          review.comments.entrySet()) {
        comments.put(pathComments.getKey(), new ArrayList<>(pathComments.getValue()));
      }
    }
    review.comments = comments;

    Map<Integer, ReviewInput> olderPatchSetReviews = new HashMap<>();
    for (CommentThread thread : selectUnresolvedOwnThreads()) {
      int patchSet = thread.root().patchSet;
      if (patchSet > reviewedPatchSet) {
        continue;
      }
      ReviewInput resolvingReview;
      if (patchSet == reviewedPatchSet) {
        if (removeMatchingComment(comments, thread)) {
          continue;
        }
        resolvingReview = review;
      } else {
        resolvingReview =
            olderPatchSetReviews.computeIfAbsent(patchSet, ignored -> createResolvingReview());
      }
      resolvingReview
          .comments
          .computeIfAbsent(thread.path(), ignored -> new ArrayList<>())
          .add(thread.createResolvingReply());
    }
    return olderPatchSetReviews;
  }

  private List<CommentThread> selectUnresolvedOwnThreads() {
    List<CommentThread> threads = new ArrayList<>();
    for (Map.Entry<String, List<CommentInfo>> pathComments : publishedCommentsByPath.entrySet()) {
      Map<String, CommentInfo> commentById =
          pathComments.getValue().stream()
              .collect(Collectors.toMap(comment -> comment.id, Function.identity()));
      Map<CommentInfo, List<CommentInfo>> commentsByRoot =
          pathComments.getValue().stream()
              .collect(Collectors.groupingBy(comment -> findRoot(comment, commentById)));
      for (Map.Entry<CommentInfo, List<CommentInfo>> threadComments : commentsByRoot.entrySet()) {
        CommentInfo root = threadComments.getKey();
        CommentInfo last =
            Collections.max(
                threadComments.getValue(), Comparator.comparing(comment -> comment.updated));
        if (isOwn(root) && Boolean.TRUE.equals(last.unresolved)) {
          threads.add(new CommentThread(pathComments.getKey(), root, last));
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

  private static boolean removeMatchingComment(
      Map<String, List<ReviewInput.CommentInput>> comments, CommentThread thread) {
    Iterator<ReviewInput.CommentInput> pathComments =
        comments.getOrDefault(thread.path(), List.of()).iterator();
    while (pathComments.hasNext()) {
      if (matches(pathComments.next(), thread.root())) {
        pathComments.remove();
        return true;
      }
    }
    return false;
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

  private static ReviewInput createResolvingReview() {
    ReviewInput review = new ReviewInput();
    review.tag = GerritReviewBuilder.REVIEW_TAG;
    review.notify = NotifyHandling.NONE;
    review.comments = new HashMap<>();
    return review;
  }

  private record CommentThread(String path, CommentInfo root, CommentInfo last) {

    ReviewInput.CommentInput createResolvingReply() {
      ReviewInput.CommentInput reply = new ReviewInput.CommentInput();
      reply.inReplyTo = last.id;
      reply.side = root.side;
      reply.line = root.line;
      reply.range = root.range;
      reply.message = RESOLUTION_MESSAGE;
      reply.unresolved = false;
      return reply;
    }
  }
}
