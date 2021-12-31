package org.jenkinsci.plugins.sonargerrit.review;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.Assert;
import org.junit.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 13:34 $Id$ */
public class ReviewWithNoIssuesTest extends ReviewResultTest implements GerritReviewTest {

  @Override
  @Test
  public void testReviewHeader() {
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, reviewResult.message);
  }

  @Override
  public void testOverrideReviewHeader() {
    getReviewConfig().setNoIssuesTitleTemplate("No Issues Header");
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals("No Issues Header", reviewResult.message);
  }

  @Override
  @Test
  public void testReviewComment() {
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(0, reviewResult.comments.size());
  }

  @Override
  public void testOverrideReviewComment() {
    getReviewConfig().setIssueCommentTemplate("No Issues Comment");
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(0, reviewResult.comments.size());
  }

  @Override
  @Test
  public void testScore() {
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertEquals(1, reviewResult.labels.get(CATEGORY).intValue());
  }

  @Override
  @Test
  public void testCategory() {
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertNotNull(reviewResult.labels.get(CATEGORY));
  }

  @Override
  @Test
  public void testOverrideScore() {
    publisher.getScoreConfig().setNoIssuesScore(2);
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertEquals(2, reviewResult.labels.get(CATEGORY).intValue());
  }

  @Override
  @Test
  public void testOverrideCategory() {
    publisher.getScoreConfig().setCategory("Other");
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertNull(reviewResult.labels.get(CATEGORY));
    Assert.assertEquals(1, reviewResult.labels.get("Other").intValue());
  }

  @Override
  @Test
  public void testOverrideScoreAndCategory() {
    publisher.getScoreConfig().setCategory("Other");
    publisher.getScoreConfig().setNoIssuesScore(2);
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertNull(reviewResult.labels.get(CATEGORY));
    Assert.assertNotNull(reviewResult.labels.get("Other"));
    Assert.assertEquals(2, reviewResult.labels.get("Other").intValue());
  }
}
