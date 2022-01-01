package org.jenkinsci.plugins.sonargerrit.config;

import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.11.2017 14:09
 *
 * <p>$Id$
 */
public class DefaultValuesTest extends BaseConfigTest {

  public static final String SEVERITY = "INFO";
  public static final boolean NEW_ONLY = false;
  public static final boolean CHANGED_ONLY = false;
  public static final String COMMENT =
      "<severity> SonarQube violation:\n\n\n<message>\n\n\nRead more: <rule_url>";
  public static final String TITLE_NO_ISSUES = "SonarQube violations have not been found.";
  public static final String TITLE_ISSUES = "<total_count> SonarQube violations have been found.";
  public static final int SCORE_NO_ISSUES = 1;
  public static final int SCORE_ISSUES = -1;
  public static final String CATEGORY = "Code-Review";
  public static final String NOTIFICATION_NO_ISSUES = "NONE";
  public static final String NOTIFICATION_ISSUES = "OWNER";
  public static final String NOTIFICATION_SCORE = "OWNER";
  public static final String SONAR_REPORT_PATH = "target/sonar/sonar-report.json";
  public static final String SONAR_URL = "http://localhost:9000";
  public static final String PROJECT_PATH = "";
  public static final String DEFAULT_INSPECTION_CONFIG_TYPE = "base";
  public static final boolean PATH_AUTO_MATCH = false;

  @Override
  protected void doTestFilterConfig() {
    Assertions.assertEquals(SEVERITY, IssueFilterConfig.DescriptorImpl.SEVERITY);
    Assertions.assertEquals(NEW_ONLY, IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY);
    Assertions.assertEquals(CHANGED_ONLY, IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY);

    IssueFilterConfig config = new IssueFilterConfig();
    Assertions.assertEquals(SEVERITY, config.getSeverity());
    Assertions.assertEquals(NEW_ONLY, config.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_ONLY, config.isChangedLinesOnly());
  }

  @Override
  protected void doTestReviewConfig() {
    Assertions.assertEquals(COMMENT, ReviewConfig.DescriptorImpl.ISSUE_COMMENT_TEMPLATE);
    Assertions.assertEquals(TITLE_NO_ISSUES, ReviewConfig.DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE);
    Assertions.assertEquals(TITLE_ISSUES, ReviewConfig.DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE);

    ReviewConfig config = new ReviewConfig();
    Assertions.assertEquals(COMMENT, config.getIssueCommentTemplate());
    Assertions.assertEquals(TITLE_NO_ISSUES, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(TITLE_ISSUES, config.getSomeIssuesTitleTemplate());

    IssueFilterConfig filterConfig = config.getIssueFilterConfig();
    Assertions.assertEquals(SEVERITY, filterConfig.getSeverity());
    Assertions.assertEquals(NEW_ONLY, filterConfig.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_ONLY, filterConfig.isChangedLinesOnly());
  }

  @Override
  protected void doTestScoreConfig() {
    Assertions.assertEquals(CATEGORY, ScoreConfig.DescriptorImpl.CATEGORY);
    Assertions.assertEquals(SCORE_NO_ISSUES, ScoreConfig.DescriptorImpl.NO_ISSUES_SCORE.intValue());
    Assertions.assertEquals(SCORE_ISSUES, ScoreConfig.DescriptorImpl.SOME_ISSUES_SCORE.intValue());

    ScoreConfig config = new ScoreConfig();
    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(SCORE_NO_ISSUES, config.getNoIssuesScore().intValue());
    Assertions.assertEquals(SCORE_ISSUES, config.getIssuesScore().intValue());

    IssueFilterConfig filterConfig = config.getIssueFilterConfig();
    Assertions.assertEquals(SEVERITY, filterConfig.getSeverity());
    Assertions.assertEquals(NEW_ONLY, filterConfig.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_ONLY, filterConfig.isChangedLinesOnly());
  }

  @Override
  protected void doTestNotificationConfig() {
    Assertions.assertEquals(
        NOTIFICATION_NO_ISSUES,
        NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES.name());
    Assertions.assertEquals(
        NOTIFICATION_ISSUES,
        NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES.name());
    Assertions.assertEquals(
        NOTIFICATION_SCORE,
        NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE.name());

    NotificationConfig config = new NotificationConfig();
    Assertions.assertEquals(NOTIFICATION_NO_ISSUES, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(NOTIFICATION_ISSUES, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(NOTIFICATION_SCORE, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  protected void doTestAuthenticationConfig() {
    GerritAuthenticationConfig config = new GerritAuthenticationConfig();
    Assertions.assertNull(config.getUsername());
    Assertions.assertNull(config.getPassword());
  }

  @Override
  protected void doTestInspectionConfig() {
    Assertions.assertEquals(SONAR_URL, InspectionConfig.DescriptorImpl.SONAR_URL);
    Assertions.assertEquals(SONAR_REPORT_PATH, SubJobConfig.DescriptorImpl.SONAR_REPORT_PATH);
    Assertions.assertEquals(PROJECT_PATH, SubJobConfig.DescriptorImpl.PROJECT_PATH);
    Assertions.assertEquals(
        DEFAULT_INSPECTION_CONFIG_TYPE,
        InspectionConfig.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE);
    Assertions.assertEquals(PATH_AUTO_MATCH, InspectionConfig.DescriptorImpl.AUTO_MATCH);
    Assertions.assertEquals(
        DEFAULT_INSPECTION_CONFIG_TYPE,
        InspectionConfig.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE);

    InspectionConfig config = new InspectionConfig();
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, config.getBaseConfig().getProjectPath());
    Assertions.assertFalse(config.isMultiConfigMode());
    Assertions.assertEquals(PATH_AUTO_MATCH, config.getBaseConfig().isAutoMatch());

    Assertions.assertNotNull(config.getSubJobConfigs());
    Assertions.assertEquals(1, config.getSubJobConfigs().size());
    SubJobConfig sConfig = new ArrayList<>(config.getSubJobConfigs()).get(0);
    Assertions.assertEquals(SONAR_REPORT_PATH, sConfig.getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, sConfig.getProjectPath());
  }
}
