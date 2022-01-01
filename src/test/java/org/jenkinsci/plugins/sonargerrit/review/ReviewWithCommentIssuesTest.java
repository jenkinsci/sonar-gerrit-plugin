package org.jenkinsci.plugins.sonargerrit.review;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 17:33
 *
 * <p>$Id$
 */
public class ReviewWithCommentIssuesTest extends ReviewResultTest implements GerritReviewTest {

  @Override
  protected void doInitialize() {
    super.doInitialize();
    commentIssues.put(
        "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
        new DummyIssue());
    commentIssues.put(
        "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager1.java",
        new DummyIssue());
  }

  @Override
  @Test
  public void testReviewHeader() {
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals("2 SonarQube violations have been found.", reviewResult.message);
  }

  @Override
  @Test
  public void testOverrideReviewHeader() {
    getReviewConfig().setSomeIssuesTitleTemplate("Some Issues Header");
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals("Some Issues Header", reviewResult.message);
  }

  @Override
  @Test
  public void testReviewComment() {
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(2, reviewResult.comments.size());
    // todo check comment >?        Assertions.assertEquals("", reviewResult.robotComments.get(0).);
  }

  @Override
  @Test
  public void testOverrideReviewComment() {
    getReviewConfig().setIssueCommentTemplate("That's an Issue!");
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(2, reviewResult.comments.size());
    // todo check text
  }

  // the rest is same as ReviewWithNoIssuesTest
  @Override
  @Test
  public void testScore() {
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(1, reviewResult.labels.size());
    Assertions.assertEquals(1, reviewResult.labels.get(CATEGORY).intValue());
  }

  @Override
  @Test
  public void testCategory() {
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(1, reviewResult.labels.size());
    Assertions.assertNotNull(reviewResult.labels.get(CATEGORY));
  }

  @Override
  @Test
  public void testOverrideScore() {
    publisher.getScoreConfig().setNoIssuesScore(2);
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(1, reviewResult.labels.size());
    Assertions.assertEquals(2, reviewResult.labels.get(CATEGORY).intValue());
  }

  @Override
  @Test
  public void testOverrideCategory() {
    publisher.getScoreConfig().setCategory("Other");
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(1, reviewResult.labels.size());
    Assertions.assertNull(reviewResult.labels.get(CATEGORY));
    Assertions.assertEquals(1, reviewResult.labels.get("Other").intValue());
  }

  @Override
  @Test
  public void testOverrideScoreAndCategory() {
    publisher.getScoreConfig().setCategory("Other");
    publisher.getScoreConfig().setNoIssuesScore(2);
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(1, reviewResult.labels.size());
    Assertions.assertNull(reviewResult.labels.get(CATEGORY));
    Assertions.assertNotNull(reviewResult.labels.get("Other"));
    Assertions.assertEquals(2, reviewResult.labels.get("Other").intValue());
  }
}
