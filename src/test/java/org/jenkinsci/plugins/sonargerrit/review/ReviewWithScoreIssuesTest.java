package org.jenkinsci.plugins.sonargerrit.review;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.config.ScoreConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 13:34
 *
 * <p>$Id$
 */
public class ReviewWithScoreIssuesTest extends ReviewResultTest implements GerritReviewTest {
  @Before
  public void initialize() {
    super.initialize();
    scoreIssues.put(
        "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
        new DummyIssue());
  }

  // review settings are same as for ReviewWithNoIssuesTest
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

  // from here its different from ReviewWithNoIssuesTest

  @Test
  public void testScore() {
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertEquals(
        -1, reviewResult.labels.get(ScoreConfig.DescriptorImpl.CATEGORY).intValue());
  }

  @Test
  public void testOverrideScore() {
    publisher.getScoreConfig().setIssuesScore(-2);
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertEquals(-2, reviewResult.labels.get(CATEGORY).intValue());
  }

  @Override
  public void testCategory() {
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertNotNull(reviewResult.labels.get(CATEGORY));
    Assert.assertEquals(-1, reviewResult.labels.get(CATEGORY).intValue());
  }

  @Test
  public void testOverrideCategory() {
    publisher.getScoreConfig().setCategory("Other");
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertNull(reviewResult.labels.get(ScoreConfig.DescriptorImpl.CATEGORY));
    Assert.assertEquals(-1, reviewResult.labels.get("Other").intValue());
  }

  @Override
  public void testOverrideScoreAndCategory() {
    publisher.getScoreConfig().setCategory("Other");
    publisher.getScoreConfig().setIssuesScore(-2);
    ReviewInput reviewResult = getReviewResult();
    Assert.assertEquals(1, reviewResult.labels.size());
    Assert.assertNull(reviewResult.labels.get(CATEGORY));
    Assert.assertNotNull(reviewResult.labels.get("Other"));
    Assert.assertEquals(-2, reviewResult.labels.get("Other").intValue());
  }
}
