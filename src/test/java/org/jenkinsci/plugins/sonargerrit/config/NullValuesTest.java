package org.jenkinsci.plugins.sonargerrit.config;

import java.util.ArrayList;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.Inspection;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.junit.jupiter.api.Assertions;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.11.2017 10:23
 *
 * <p>$Id$
 */
public class NullValuesTest extends DetailedConfigTest {

  @Override
  protected void doTestSeverity() {
    IssueFilterConfig config = new IssueFilterConfig();
    config.setSeverity(null);
    Assertions.assertEquals(SEVERITY, config.getSeverity());
  }

  @Override
  protected void doTestNewOnly() {
    // boolean doesn't allow null
  }

  @Override
  protected void doTestChangedLinesOnly() {
    // boolean doesn't allow null
  }

  @Override
  protected void doTestFilterConfig() {
    ReviewConfig config = new ReviewConfig();
    config.setIssueFilterConfig(null);
    Assertions.assertNotNull(config.getIssueFilterConfig());
    Assertions.assertEquals(SEVERITY, config.getIssueFilterConfig().getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.getIssueFilterConfig().isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.getIssueFilterConfig().isChangedLinesOnly());

    IssueFilterConfig fconfig = new IssueFilterConfig(null, false, false);
    Assertions.assertEquals(SEVERITY, fconfig.getSeverity());
  }

  @Override
  protected void doTestNoIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setNoIssuesTitleTemplate(null);
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
  }

  @Override
  protected void doTestSomeIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setSomeIssuesTitleTemplate(null);
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
  }

  @Override
  protected void doTestIssuesCommentTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setIssueCommentTemplate(null);
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Override
  protected void doTestReviewConfig() {
    SonarToGerritPublisher publisher = new SonarToGerritPublisher();
    publisher.setReviewConfig(null);
    ReviewConfig config = publisher.getReviewConfig();
    Assertions.assertNotNull(config);
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assertions.assertEquals(SEVERITY, config.getIssueFilterConfig().getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.getIssueFilterConfig().isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.getIssueFilterConfig().isChangedLinesOnly());

    config = new ReviewConfig(null, null, null, null, false);
    Assertions.assertNotNull(config.getIssueFilterConfig());
    Assertions.assertEquals(SEVERITY, config.getIssueFilterConfig().getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.getIssueFilterConfig().isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.getIssueFilterConfig().isChangedLinesOnly());
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assertions.assertEquals(ISSUE_OMIT_DUPLICATE_COMMENTS, config.isOmitDuplicateComments());
  }

  @Override
  protected void doTestCategory() {
    ScoreConfig config = new ScoreConfig();
    config.setCategory(null);
    Assertions.assertEquals(CATEGORY, config.getCategory());
  }

  @Override
  protected void doTestNoIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    config.setNoIssuesScore(null);
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
  }

  @Override
  protected void doTestSomeIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    config.setIssuesScore(null);
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());
  }

  @Override
  protected void doTestScoreConfig() {
    SonarToGerritPublisher publisher = new SonarToGerritPublisher();
    publisher.setScoreConfig(null);
    Assertions.assertNull(publisher.getScoreConfig());

    ScoreConfig config = new ScoreConfig(null, null, null, null);
    Assertions.assertNotNull(config.getIssueFilterConfig());
    Assertions.assertEquals(SEVERITY, config.getIssueFilterConfig().getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.getIssueFilterConfig().isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.getIssueFilterConfig().isChangedLinesOnly());
    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());
  }

  @Override
  protected void doTestNoIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNoIssuesNotificationRecipient(null);
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
  }

  @Override
  protected void doTestIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setCommentedIssuesNotificationRecipient(null);
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
  }

  @Override
  protected void doTestNegativeScoreNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNegativeScoreNotificationRecipient(null);
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  protected void doTestNotificationConfig() {
    SonarToGerritPublisher publisher = new SonarToGerritPublisher();
    publisher.setNotificationConfig(null);
    NotificationConfig config = publisher.getNotificationConfig();
    Assertions.assertNotNull(config);
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    config = new NotificationConfig(null, null, "");
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  protected void doTestSonarUrl() {
    Inspection config = new Inspection();
    config.setServerURL(null);
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
  }

  @Override
  protected void doTestSonarReportPath() {
    SubJobConfig config = new SubJobConfig();
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
    config.setSonarReportPath(null);
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
  }

  @Override
  protected void doTestInspectionConfig() {
    SonarToGerritPublisher publisher = new SonarToGerritPublisher();
    publisher.setInspectionConfig(null);
    Inspection config = publisher.getInspectionConfig();
    Assertions.assertNotNull(config);
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, config.getBaseConfig().getProjectPath());
    Assertions.assertEquals(PATH_AUTO_MATCH, config.getBaseConfig().isAutoMatch());
    Assertions.assertNotNull(config.getSubJobConfigs());
    Assertions.assertEquals(1, config.getSubJobConfigs().size());
  }

  @Override
  protected void doTestSubJobConfig() {
    Inspection config = new Inspection();
    config.setBaseConfig(null);
    Assertions.assertNotNull(config.getBaseConfig());
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, config.getBaseConfig().getProjectPath());
    Assertions.assertEquals(PATH_AUTO_MATCH, config.getBaseConfig().isAutoMatch());

    Assertions.assertNotNull(config.getSubJobConfigs());
    Assertions.assertEquals(1, config.getSubJobConfigs().size());
    SubJobConfig subJobConfig = new ArrayList<>(config.getSubJobConfigs()).get(0);
    Assertions.assertEquals(SONAR_REPORT_PATH, subJobConfig.getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, subJobConfig.getProjectPath());
    Assertions.assertFalse(subJobConfig.isAutoMatch());
  }

  @Override
  protected void doTestProjectConfig() {
    SubJobConfig config = new SubJobConfig();
    Assertions.assertEquals(PROJECT_PATH, config.getProjectPath());
    config.setProjectPath(null);
    Assertions.assertEquals(PROJECT_PATH, config.getProjectPath());
  }

  @Override
  protected void doTestAuthenticationConfig() {}
}
