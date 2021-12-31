package org.jenkinsci.plugins.sonargerrit.config;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 21:35
 *
 * <p>$Id$
 */
public interface DetailedConfigTest extends BaseConfigTest {
  String NO_ISSUES_TITLE_TEMPLATE = ReviewConfig.DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE;
  Integer NO_ISSUES_SCORE = ScoreConfig.DescriptorImpl.NO_ISSUES_SCORE;
  Integer SOME_ISSUES_SCORE = ScoreConfig.DescriptorImpl.SOME_ISSUES_SCORE;
  String ISSUE_COMMENT_TEMPLATE = ReviewConfig.DescriptorImpl.ISSUE_COMMENT_TEMPLATE;
  String SOME_ISSUES_TITLE_TEMPLATE = ReviewConfig.DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE;
  String CATEGORY = ScoreConfig.DescriptorImpl.CATEGORY;
  String NO_ISSUES_NOTIFICATION =
      NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES.name();
  String ISSUES_NOTIFICATION =
      NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES.name();
  String SCORE_NOTIFICATION =
      NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE.name();
  String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;
  String SONAR_REPORT_PATH = SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH;
  String PROJECT_PATH = SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH;
  String SEVERITY = IssueFilterConfig.DescriptorImpl.SEVERITY;
  String DEFAULT_INSPECTION_CONFIG_TYPE =
      InspectionConfig.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE;
  boolean NEW_ISSUES_ONLY = IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY;
  boolean CHANGED_LINES_ONLY = IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY;
  boolean PATH_AUTO_MATCH = InspectionConfig.DescriptorImpl.AUTO_MATCH;

  // IssueFilterConfig
  @SuppressWarnings(value = "unused")
  void testSeverity();

  @SuppressWarnings(value = "unused")
  void testNewOnly();

  @SuppressWarnings(value = "unused")
  void testChangedLinesOnly();

  // ReviewConfig

  @SuppressWarnings(value = "unused")
  void testNoIssuesTitleTemplate();

  @SuppressWarnings(value = "unused")
  void testSomeIssuesTitleTemplate();

  @SuppressWarnings(value = "unused")
  void testIssuesCommentTemplate();

  // ScoreConfig

  @SuppressWarnings(value = "unused")
  void testCategory();

  @SuppressWarnings(value = "unused")
  void testNoIssuesScoreScore();

  @SuppressWarnings(value = "unused")
  void testSomeIssuesScoreScore();

  // NotificationConfig

  @SuppressWarnings(value = "unused")
  void testNoIssuesNotificationRecipient();

  @SuppressWarnings(value = "unused")
  void testIssuesNotificationRecipient();

  @SuppressWarnings(value = "unused")
  void testNegativeScoreNotificationRecipient();

  // Sonar config

  @SuppressWarnings(value = "unused")
  void testSubJobConfig();

  @SuppressWarnings(value = "unused")
  void testSonarUrl();

  @SuppressWarnings(value = "unused")
  void testSonarReportPath();

  @SuppressWarnings(value = "unused")
  void testProjectConfig();
}
