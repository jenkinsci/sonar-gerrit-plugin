package org.jenkinsci.plugins.sonargerrit.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.11.2017 14:09
 * <p>
 * $Id$
 */
public class DefaultValuesTest implements BaseConfigTest {

    public static final String SEVERITY = "INFO";
    public static final boolean NEW_ONLY = false;
    public static final boolean CHANGED_ONLY = false;
    public static final String COMMENT = "<severity> SonarQube violation:\n\n\n<message>\n\n\nRead more: <rule_url>";
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
    public static final String MULTI_TYPE = "multi";
    public static final String BASE_TYPE = "base";

    @Test
    public void testFilterConfig() {
        Assert.assertEquals(SEVERITY, IssueFilterConfig.DescriptorImpl.SEVERITY);
        Assert.assertEquals(NEW_ONLY, IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY);
        Assert.assertEquals(CHANGED_ONLY, IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY);

        IssueFilterConfig config = new IssueFilterConfig();
        Assert.assertEquals(SEVERITY, config.getSeverity());
        Assert.assertEquals(NEW_ONLY, config.isNewIssuesOnly());
        Assert.assertEquals(CHANGED_ONLY, config.isChangedLinesOnly());
    }

    @Test
    public void testReviewConfig() {
        Assert.assertEquals(COMMENT, ReviewConfig.DescriptorImpl.ISSUE_COMMENT_TEMPLATE);
        Assert.assertEquals(TITLE_NO_ISSUES, ReviewConfig.DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE);
        Assert.assertEquals(TITLE_ISSUES, ReviewConfig.DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE);

        ReviewConfig config = new ReviewConfig();
        Assert.assertEquals(COMMENT, config.getIssueCommentTemplate());
        Assert.assertEquals(TITLE_NO_ISSUES, config.getNoIssuesTitleTemplate());
        Assert.assertEquals(TITLE_ISSUES, config.getSomeIssuesTitleTemplate());

        IssueFilterConfig filterConfig = config.getIssueFilterConfig();
        Assert.assertEquals(SEVERITY, filterConfig.getSeverity());
        Assert.assertEquals(NEW_ONLY, filterConfig.isNewIssuesOnly());
        Assert.assertEquals(CHANGED_ONLY, filterConfig.isChangedLinesOnly());
    }

    @Test
    public void testScoreConfig() {
        Assert.assertEquals(CATEGORY, ScoreConfig.DescriptorImpl.CATEGORY);
        Assert.assertEquals(SCORE_NO_ISSUES, ScoreConfig.DescriptorImpl.NO_ISSUES_SCORE.intValue());
        Assert.assertEquals(SCORE_ISSUES, ScoreConfig.DescriptorImpl.SOME_ISSUES_SCORE.intValue());

        ScoreConfig config = new ScoreConfig();
        Assert.assertEquals(CATEGORY, config.getCategory());
        Assert.assertEquals(SCORE_NO_ISSUES, config.getNoIssuesScore().intValue());
        Assert.assertEquals(SCORE_ISSUES, config.getIssuesScore().intValue());

        IssueFilterConfig filterConfig = config.getIssueFilterConfig();
        Assert.assertEquals(SEVERITY, filterConfig.getSeverity());
        Assert.assertEquals(NEW_ONLY, filterConfig.isNewIssuesOnly());
        Assert.assertEquals(CHANGED_ONLY, filterConfig.isChangedLinesOnly());
    }

    @Test
    public void testNotificationConfig() {
        Assert.assertEquals(NOTIFICATION_NO_ISSUES, NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES.name());
        Assert.assertEquals(NOTIFICATION_ISSUES, NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES.name());
        Assert.assertEquals(NOTIFICATION_SCORE, NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE.name());

        NotificationConfig config = new NotificationConfig();
        Assert.assertEquals(NOTIFICATION_NO_ISSUES, config.getNoIssuesNotificationRecipient());
        Assert.assertEquals(NOTIFICATION_ISSUES, config.getCommentedIssuesNotificationRecipient());
        Assert.assertEquals(NOTIFICATION_SCORE, config.getNegativeScoreNotificationRecipient());

    }

    @Test
    public void testAuthenticationConfig() {
        GerritAuthenticationConfig config = new GerritAuthenticationConfig();
        Assert.assertNull(config.getUsername());
        Assert.assertNull(config.getPassword());
    }

    @Test
    public void testInspectionConfig() {
        Assert.assertEquals(SONAR_URL, InspectionConfig.DescriptorImpl.SONAR_URL);
        Assert.assertEquals(SONAR_REPORT_PATH, SubJobConfig.DescriptorImpl.SONAR_REPORT_PATH);
        Assert.assertEquals(PROJECT_PATH, SubJobConfig.DescriptorImpl.PROJECT_PATH);
        Assert.assertEquals(DEFAULT_INSPECTION_CONFIG_TYPE, InspectionConfig.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE);
        Assert.assertEquals(PATH_AUTO_MATCH, InspectionConfig.DescriptorImpl.AUTO_MATCH);
        Assert.assertEquals(DEFAULT_INSPECTION_CONFIG_TYPE, InspectionConfig.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE);

        InspectionConfig config = new InspectionConfig();
        Assert.assertEquals(SONAR_URL, config.getServerURL());
        Assert.assertEquals(SONAR_REPORT_PATH, config.getBaseConfig().getSonarReportPath());
        Assert.assertEquals(PROJECT_PATH, config.getBaseConfig().getProjectPath());
        Assert.assertFalse(config.isMultiConfigMode());
        Assert.assertEquals(PATH_AUTO_MATCH, config.getBaseConfig().isAutoMatch());

        Assert.assertNotNull(config.getSubJobConfigs());
        Assert.assertEquals(1, config.getSubJobConfigs().size());
        SubJobConfig sConfig = new ArrayList<>(config.getSubJobConfigs()).get(0);
        Assert.assertEquals(SONAR_REPORT_PATH, sConfig.getSonarReportPath());
        Assert.assertEquals(PROJECT_PATH, sConfig.getProjectPath());
    }

}
