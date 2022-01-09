package org.jenkinsci.plugins.sonargerrit.config;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilterConfig;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 21:35
 *
 * <p>$Id$
 */
public abstract class DetailedConfigTest extends BaseConfigTest {
  protected static final String NO_ISSUES_TITLE_TEMPLATE =
      ReviewConfig.DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE;
  protected static final Integer NO_ISSUES_SCORE = ScoreConfig.DescriptorImpl.NO_ISSUES_SCORE;
  protected static final Integer SOME_ISSUES_SCORE = ScoreConfig.DescriptorImpl.SOME_ISSUES_SCORE;
  protected static final String ISSUE_COMMENT_TEMPLATE =
      ReviewConfig.DescriptorImpl.ISSUE_COMMENT_TEMPLATE;
  protected static final String SOME_ISSUES_TITLE_TEMPLATE =
      ReviewConfig.DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE;
  protected static final String CATEGORY = ScoreConfig.DescriptorImpl.CATEGORY;
  protected static final String NO_ISSUES_NOTIFICATION =
      NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES.name();
  protected static final String ISSUES_NOTIFICATION =
      NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES.name();
  protected static final String SCORE_NOTIFICATION =
      NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE.name();
  protected static final String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;
  protected static final String SONAR_REPORT_PATH =
      SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH;
  protected static final String PROJECT_PATH = SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH;
  protected static final String SEVERITY = IssueFilterConfig.DescriptorImpl.SEVERITY;
  protected static final String DEFAULT_INSPECTION_CONFIG_TYPE =
      InspectionConfig.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE;
  protected static final boolean NEW_ISSUES_ONLY = IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY;
  protected static final boolean CHANGED_LINES_ONLY =
      IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY;
  protected static final boolean PATH_AUTO_MATCH = InspectionConfig.DescriptorImpl.AUTO_MATCH;

  // IssueFilterConfig

  @Test
  public final void testSeverity() {
    doTestSeverity();
  }

  protected abstract void doTestSeverity();

  @Test
  public final void testNewOnly() {
    doTestNewOnly();
  }

  protected abstract void doTestNewOnly();

  @Test
  public final void testChangedLinesOnly() {
    doTestChangedLinesOnly();
  }

  protected abstract void doTestChangedLinesOnly();

  // ReviewConfig

  @Test
  public final void testNoIssuesTitleTemplate() {
    doTestNoIssuesTitleTemplate();
  }

  protected abstract void doTestNoIssuesTitleTemplate();

  @Test
  public final void testSomeIssuesTitleTemplate() {
    doTestSomeIssuesTitleTemplate();
  }

  protected abstract void doTestSomeIssuesTitleTemplate();

  @Test
  public final void testIssuesCommentTemplate() {
    doTestIssuesCommentTemplate();
  }

  protected abstract void doTestIssuesCommentTemplate();

  // ScoreConfig

  @Test
  public final void testCategory() {
    doTestCategory();
  }

  protected abstract void doTestCategory();

  @Test
  public final void testNoIssuesScoreScore() {
    doTestNoIssuesScoreScore();
  }

  protected abstract void doTestNoIssuesScoreScore();

  @Test
  public final void testSomeIssuesScoreScore() {
    doTestSomeIssuesScoreScore();
  }

  protected abstract void doTestSomeIssuesScoreScore();

  // NotificationConfig

  @Test
  public final void testNoIssuesNotificationRecipient() {
    doTestNoIssuesNotificationRecipient();
  }

  protected abstract void doTestNoIssuesNotificationRecipient();

  @Test
  public final void testIssuesNotificationRecipient() {
    doTestIssuesNotificationRecipient();
  }

  protected abstract void doTestIssuesNotificationRecipient();

  @Test
  public final void testNegativeScoreNotificationRecipient() {
    doTestNegativeScoreNotificationRecipient();
  }

  protected abstract void doTestNegativeScoreNotificationRecipient();

  // Sonar config

  @Test
  public final void testSubJobConfig() {
    doTestSubJobConfig();
  }

  protected abstract void doTestSubJobConfig();

  @Test
  public final void testSonarUrl() {
    doTestSonarUrl();
  }

  protected abstract void doTestSonarUrl();

  @Test
  public final void testSonarReportPath() {
    doTestSonarReportPath();
  }

  protected abstract void doTestSonarReportPath();

  @Test
  public final void testProjectConfig() {
    doTestProjectConfig();
  }

  protected abstract void doTestProjectConfig();
}
