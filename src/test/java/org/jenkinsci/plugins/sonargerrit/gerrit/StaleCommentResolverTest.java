package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.NotifyHandling;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.AccountInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.CommentInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@NullMarked
class StaleCommentResolverTest {

  private static final int OWN_ACCOUNT_ID = 1;
  private static final int OTHER_ACCOUNT_ID = 2;
  private static final String PATH = "Foo.java";

  @Test
  @DisplayName(
      "Does not comment again an issue with an unresolved thread on the reviewed patch set")
  void test1() {
    StaleCommentResolver resolver =
        new StaleCommentResolver(
            OWN_ACCOUNT_ID,
            2,
            Map.of(
                PATH,
                List.of(
                    createComment(
                        "1", null, 2, OWN_ACCOUNT_ID, GerritReviewBuilder.REVIEW_TAG, 10, true))));
    ReviewInput review = createReview(createNewComment(10, "Issue 10\n"), createNewComment(20));

    Map<Integer, ReviewInput> olderPatchSetReviews = resolver.resolveStaleThreads(review);

    assertThat(olderPatchSetReviews).isEmpty();
    assertThat(review.comments.get(PATH)).extracting(comment -> comment.line).containsExactly(20);
  }

  @Test
  @DisplayName("Resolves an unresolved thread of the reviewed patch set whose issue is gone")
  void test2() {
    StaleCommentResolver resolver =
        new StaleCommentResolver(
            OWN_ACCOUNT_ID,
            2,
            Map.of(
                PATH,
                List.of(
                    createComment(
                        "1", null, 2, OWN_ACCOUNT_ID, GerritReviewBuilder.REVIEW_TAG, 10, true))));
    ReviewInput review = createReview();

    Map<Integer, ReviewInput> olderPatchSetReviews = resolver.resolveStaleThreads(review);

    assertThat(olderPatchSetReviews).isEmpty();
    assertThat(review.comments.get(PATH))
        .extracting(
            reply -> reply.inReplyTo,
            reply -> reply.line,
            reply -> reply.unresolved,
            reply -> reply.message)
        .containsExactly(tuple("1", 10, false, StaleCommentResolver.RESOLUTION_MESSAGE));
  }

  @Test
  @DisplayName("Resolves an unresolved thread of an older patch set on that patch set")
  void test3() {
    StaleCommentResolver resolver =
        new StaleCommentResolver(
            OWN_ACCOUNT_ID,
            2,
            Map.of(
                PATH,
                List.of(
                    createComment(
                        "1", null, 1, OWN_ACCOUNT_ID, GerritReviewBuilder.REVIEW_TAG, 10, true),
                    createComment("2", "1", 1, OTHER_ACCOUNT_ID, null, 10, true))));
    ReviewInput review = createReview(createNewComment(10));

    Map<Integer, ReviewInput> olderPatchSetReviews = resolver.resolveStaleThreads(review);

    assertThat(review.comments.get(PATH))
        .extracting(comment -> comment.inReplyTo)
        .containsExactly((String) null);
    assertThat(olderPatchSetReviews).containsOnlyKeys(1);
    ReviewInput olderReview = olderPatchSetReviews.get(1);
    assertThat(olderReview.tag).isEqualTo(GerritReviewBuilder.REVIEW_TAG);
    assertThat(olderReview.notify).isEqualTo(NotifyHandling.NONE);
    assertThat(olderReview.labels).isNull();
    assertThat(olderReview.comments.get(PATH))
        .extracting(reply -> reply.inReplyTo, reply -> reply.unresolved)
        .containsExactly(tuple("2", false));
  }

  @Test
  @DisplayName("Leaves alone resolved threads, foreign threads and threads of newer patch sets")
  void test4() {
    StaleCommentResolver resolver =
        new StaleCommentResolver(
            OWN_ACCOUNT_ID,
            2,
            Map.of(
                PATH,
                List.of(
                    createComment(
                        "1", null, 2, OWN_ACCOUNT_ID, GerritReviewBuilder.REVIEW_TAG, 10, true),
                    createComment("2", "1", 2, OTHER_ACCOUNT_ID, null, 10, false),
                    createComment("3", null, 2, OTHER_ACCOUNT_ID, null, 30, true),
                    createComment("4", null, 2, OWN_ACCOUNT_ID, null, 40, true),
                    createComment(
                        "5", null, 3, OWN_ACCOUNT_ID, GerritReviewBuilder.REVIEW_TAG, 50, true))));
    ReviewInput review = createReview();

    Map<Integer, ReviewInput> olderPatchSetReviews = resolver.resolveStaleThreads(review);

    assertThat(olderPatchSetReviews).isEmpty();
    assertThat(review.comments).isEmpty();
  }

  @Test
  @DisplayName("Comments again an issue whose thread was resolved")
  void test5() {
    StaleCommentResolver resolver =
        new StaleCommentResolver(
            OWN_ACCOUNT_ID,
            2,
            Map.of(
                PATH,
                List.of(
                    createComment(
                        "1", null, 2, OWN_ACCOUNT_ID, GerritReviewBuilder.REVIEW_TAG, 10, true),
                    createComment(
                        "2", "1", 2, OWN_ACCOUNT_ID, GerritReviewBuilder.REVIEW_TAG, 10, false))));
    ReviewInput review = createReview(createNewComment(10));
    review.omitDuplicateComments = true;

    resolver.resolveStaleThreads(review);

    assertThat(review.omitDuplicateComments).isFalse();
    assertThat(review.comments.get(PATH))
        .extracting(comment -> comment.inReplyTo)
        .containsExactly((String) null);
  }

  private static ReviewInput createReview(ReviewInput.CommentInput... comments) {
    ReviewInput review = new ReviewInput();
    review.comments = new HashMap<>();
    if (comments.length > 0) {
      review.comments.put(PATH, new ArrayList<>(List.of(comments)));
    }
    return review;
  }

  private static ReviewInput.CommentInput createNewComment(int line) {
    return createNewComment(line, "Issue " + line);
  }

  private static ReviewInput.CommentInput createNewComment(int line, String message) {
    ReviewInput.CommentInput comment = new ReviewInput.CommentInput();
    comment.line = line;
    comment.message = message;
    comment.unresolved = true;
    return comment;
  }

  private static CommentInfo createComment(
      String id,
      @Nullable String inReplyTo,
      int patchSet,
      int authorId,
      @Nullable String tag,
      int line,
      boolean unresolved) {
    CommentInfo comment = new CommentInfo();
    comment.id = id;
    comment.inReplyTo = inReplyTo;
    comment.patchSet = patchSet;
    comment.author = new AccountInfo(authorId);
    comment.tag = tag;
    comment.line = line;
    comment.message = "Issue " + line;
    comment.unresolved = unresolved;
    comment.updated = new Timestamp(Long.parseLong(id));
    return comment;
  }
}
