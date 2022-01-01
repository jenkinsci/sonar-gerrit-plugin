package org.jenkinsci.plugins.sonargerrit.config;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.11.2017 22:15
 *
 * <p>$Id$
 */
public class WrongValuesTest implements DetailedConfigTest {
  @Override
  @Test
  public void testSeverity() {
    IssueFilterConfig config = new IssueFilterConfig();
    config.setSeverity("Test");
    Assertions.assertEquals(SEVERITY, config.getSeverity());
  }

  @Override
  @Test
  public void testNewOnly() {
    // nothing to check here
  }

  @Override
  @Test
  public void testChangedLinesOnly() {
    // nothing to check here
  }

  @Override
  @Test
  public void testNoIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setNoIssuesTitleTemplate("");
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
  }

  @Override
  @Test
  public void testSomeIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setSomeIssuesTitleTemplate("");
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
  }

  @Override
  @Test
  public void testIssuesCommentTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setIssueCommentTemplate("");
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Override
  @Test
  public void testCategory() {
    ScoreConfig config = new ScoreConfig();
    config.setCategory("");
    Assertions.assertEquals(CATEGORY, config.getCategory());
  }

  @Override
  @Test
  public void testNoIssuesScoreScore() {
    //        ScoreConfig config = new ScoreConfig();
    //        config.setNoIssuesScore(-50); ?

    SonarToGerritPublisher p = new SonarToGerritPublisher();
    p.setPostScore(true);
    p.setNoIssuesScore("test");
    Assertions.assertEquals(NO_ISSUES_SCORE, p.getScoreConfig().getNoIssuesScore());
  }

  @Override
  @Test
  public void testSomeIssuesScoreScore() {
    //        ScoreConfig config = new ScoreConfig();
    //        config.setIssuesScore(-50); ?

    SonarToGerritPublisher p = new SonarToGerritPublisher();
    p.setPostScore(true);
    p.setIssuesScore("test");
    Assertions.assertEquals(SOME_ISSUES_SCORE, p.getScoreConfig().getIssuesScore());
  }

  @Override
  @Test
  public void testNoIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNoIssuesNotificationRecipient("");
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
  }

  @Override
  @Test
  public void testIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setCommentedIssuesNotificationRecipient("");
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
  }

  @Override
  @Test
  public void testNegativeScoreNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNegativeScoreNotificationRecipient("");
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  @Test
  public void testSonarUrl() {
    InspectionConfig config = new InspectionConfig();
    config.setServerURL("");
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
  }

  @Override
  @Test
  public void testSonarReportPath() {
    SubJobConfig config = new SubJobConfig();
    config.setSonarReportPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getSonarReportPath());

    config.setSonarReportPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getSonarReportPath());
  }

  @Override
  @Test
  public void testFilterConfig() {
    // nope
  }

  @Override
  @Test
  public void testReviewConfig() {
    // nope
  }

  @Override
  @Test
  public void testScoreConfig() {
    // nope
  }

  @Override
  @Test
  public void testNotificationConfig() {
    // nope
  }

  @Override
  @Test
  public void testAuthenticationConfig() {
    // nope
  }

  @Override
  @Test
  public void testInspectionConfig() {
    InspectionConfig config = new InspectionConfig();
    config.setType("test");
    Assertions.assertFalse(config.isType("test"));
    Assertions.assertTrue(config.isType(DEFAULT_INSPECTION_CONFIG_TYPE));

    config.setType("multi");
    Assertions.assertTrue(config.isType("multi"));
    config.setType("test");
    Assertions.assertFalse(config.isType("test"));
    Assertions.assertTrue(config.isType("multi"));
  }

  @Override
  @Test
  public void testSubJobConfig() {}

  @Override
  @Test
  public void testProjectConfig() {
    SubJobConfig config = new SubJobConfig();
    config.setProjectPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getProjectPath());

    config.setProjectPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getProjectPath());
  }
}
