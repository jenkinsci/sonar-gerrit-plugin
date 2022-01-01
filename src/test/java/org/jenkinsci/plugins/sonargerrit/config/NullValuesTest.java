package org.jenkinsci.plugins.sonargerrit.config;

import java.util.ArrayList;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.11.2017 10:23
 *
 * <p>$Id$
 */
public class NullValuesTest implements DetailedConfigTest {

  @Override
  @Test
  public void testSeverity() {
    IssueFilterConfig config = new IssueFilterConfig();
    config.setSeverity(null);
    Assertions.assertEquals(SEVERITY, config.getSeverity());
  }

  @Override
  public void testNewOnly() {
    // boolean doesn't allow null
  }

  @Override
  public void testChangedLinesOnly() {
    // boolean doesn't allow null
  }

  @Override
  @Test
  public void testFilterConfig() {
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
  @Test
  public void testNoIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setNoIssuesTitleTemplate(null);
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
  }

  @Override
  @Test
  public void testSomeIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setSomeIssuesTitleTemplate(null);
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
  }

  @Override
  public void testIssuesCommentTemplate() {
    ReviewConfig config = new ReviewConfig();
    config.setIssueCommentTemplate(null);
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Override
  @Test
  public void testReviewConfig() {
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

    config = new ReviewConfig(null, null, null, null);
    Assertions.assertNotNull(config.getIssueFilterConfig());
    Assertions.assertEquals(SEVERITY, config.getIssueFilterConfig().getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.getIssueFilterConfig().isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.getIssueFilterConfig().isChangedLinesOnly());
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Override
  @Test
  public void testCategory() {
    ScoreConfig config = new ScoreConfig();
    config.setCategory(null);
    Assertions.assertEquals(CATEGORY, config.getCategory());
  }

  @Override
  @Test
  public void testNoIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    config.setNoIssuesScore(null);
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
  }

  @Override
  @Test
  public void testSomeIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    config.setIssuesScore(null);
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());
  }

  @Override
  @Test
  public void testScoreConfig() {
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
  @Test
  public void testNoIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNoIssuesNotificationRecipient(null);
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
  }

  @Override
  @Test
  public void testIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setCommentedIssuesNotificationRecipient(null);
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
  }

  @Override
  @Test
  public void testNegativeScoreNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    config.setNegativeScoreNotificationRecipient(null);
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  @Test
  public void testNotificationConfig() {
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
  @Test
  public void testSonarUrl() {
    InspectionConfig config = new InspectionConfig();
    config.setServerURL(null);
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
  }

  @Override
  @Test
  public void testSonarReportPath() {
    SubJobConfig config = new SubJobConfig();
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
    config.setSonarReportPath(null);
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
  }

  @Override
  @Test
  public void testInspectionConfig() {
    SonarToGerritPublisher publisher = new SonarToGerritPublisher();
    publisher.setInspectionConfig(null);
    InspectionConfig config = publisher.getInspectionConfig();
    Assertions.assertNotNull(config);
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, config.getBaseConfig().getProjectPath());
    Assertions.assertEquals(PATH_AUTO_MATCH, config.getBaseConfig().isAutoMatch());
    Assertions.assertNotNull(config.getSubJobConfigs());
    Assertions.assertEquals(1, config.getSubJobConfigs().size());
    SubJobConfig subJobConfig = new ArrayList<>(config.getAllSubJobConfigs()).get(0);
    Assertions.assertEquals(SONAR_REPORT_PATH, subJobConfig.getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, subJobConfig.getProjectPath());
    Assertions.assertFalse(subJobConfig.isAutoMatch());
  }

  @Override
  @Test
  public void testSubJobConfig() {
    InspectionConfig config = new InspectionConfig();
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
  @Test
  public void testProjectConfig() {
    SubJobConfig config = new SubJobConfig();
    Assertions.assertEquals(PROJECT_PATH, config.getProjectPath());
    config.setProjectPath(null);
    Assertions.assertEquals(PROJECT_PATH, config.getProjectPath());
  }

  // @Test
  @Override
  public void testAuthenticationConfig() {}
}
