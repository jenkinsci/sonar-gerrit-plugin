package org.jenkinsci.plugins.sonargerrit.config;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.SubJobConfig;
import org.junit.jupiter.api.Assertions;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.11.2017 22:15
 *
 * <p>$Id$
 */
public class WrongValuesTest extends DetailedConfigTest {
  @Override
  protected void doTestSeverity() {
    IssueFilterConfig config = new IssueFilterConfig();
    config.setSeverity("Test");
    Assertions.assertEquals(SEVERITY, config.getSeverity());
  }

  @Override
  protected void doTestNewOnly() {
    // nothing to check here
  }

  @Override
  protected void doTestChangedLinesOnly() {
    // nothing to check here
  }

  @Override
  protected void doTestNoIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setNoIssuesTitleTemplate("");
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
  }

  @Override
  protected void doTestSomeIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setSomeIssuesTitleTemplate("");
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
  }

  @Override
  protected void doTestIssuesCommentTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setIssueCommentTemplate("");
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Override
  protected void doTestCategory() {
    ScoreConfig config = new ScoreConfig();
    config.setCategory("");
    Assertions.assertEquals(CATEGORY, config.getCategory());
  }

  @Override
  protected void doTestNoIssuesScoreScore() {
    SonarToGerritPublisher p = new SonarToGerritPublisher();
    p.setPostScore(true);
    p.setNoIssuesScore("test");
    Assertions.assertEquals(NO_ISSUES_SCORE, p.getScoreConfig().getNoIssuesScore());
  }

  @Override
  protected void doTestSomeIssuesScoreScore() {
    SonarToGerritPublisher p = new SonarToGerritPublisher();
    p.setPostScore(true);
    p.setIssuesScore("test");
    Assertions.assertEquals(SOME_ISSUES_SCORE, p.getScoreConfig().getIssuesScore());
  }

  @Override
  protected void doTestNoIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNoIssuesNotificationRecipient("");
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
  }

  @Override
  protected void doTestIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setCommentedIssuesNotificationRecipient("");
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
  }

  @Override
  protected void doTestNegativeScoreNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNegativeScoreNotificationRecipient("");
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  protected void doTestSonarUrl() {
    InspectionConfig config = new InspectionConfig();
    config.setServerURL("");
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
  }

  @Override
  protected void doTestSonarReportPath() {
    SubJobConfig config = new SubJobConfig();
    config.setSonarReportPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getSonarReportPath());

    config.setSonarReportPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getSonarReportPath());
  }

  @Override
  protected void doTestFilterConfig() {
    // nope
  }

  @Override
  protected void doTestReviewConfig() {
    // nope
  }

  @Override
  protected void doTestScoreConfig() {
    // nope
  }

  @Override
  protected void doTestNotificationConfig() {
    // nope
  }

  @Override
  protected void doTestAuthenticationConfig() {
    // nope
  }

  @Override
  protected void doTestInspectionConfig() {
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
  protected void doTestSubJobConfig() {}

  @Override
  protected void doTestProjectConfig() {
    SubJobConfig config = new SubJobConfig();
    config.setProjectPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getProjectPath());

    config.setProjectPath("fghdfh^%&$(&*IOUM V");
    Assertions.assertEquals("fghdfh^%&$(&*IOUM V", config.getProjectPath());
  }
}
