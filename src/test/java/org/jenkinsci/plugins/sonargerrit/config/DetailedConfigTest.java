package org.jenkinsci.plugins.sonargerrit.config;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 21:35
 * <p>
 * $Id$
 */
public interface DetailedConfigTest extends BaseConfigTest {
    String NO_ISSUES_TITLE_TEMPLATE = ReviewConfig.DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE;
    Integer NO_ISSUES_SCORE = ScoreConfig.DescriptorImpl.NO_ISSUES_SCORE;
    Integer SOME_ISSUES_SCORE = ScoreConfig.DescriptorImpl.SOME_ISSUES_SCORE;
    String ISSUE_COMMENT_TEMPLATE = ReviewConfig.DescriptorImpl.ISSUE_COMMENT_TEMPLATE;
    String SOME_ISSUES_TITLE_TEMPLATE = ReviewConfig.DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE;
    boolean FAIL_ONLY = ReviewConfig.DescriptorImpl.FAIL_ONLY;
    String CATEGORY = ScoreConfig.DescriptorImpl.CATEGORY;
    String NO_ISSUES_NOTIFICATION = NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES.name();
    String ISSUES_NOTIFICATION = NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES.name();
    String SCORE_NOTIFICATION = NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE.name();
    String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;
    String SONAR_REPORT_PATH = SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH;
    String PROJECT_PATH = SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH;
    String SEVERITY = IssueFilterConfig.DescriptorImpl.SEVERITY;
    boolean NEW_ISSUES_ONLY = IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY;
    boolean CHANGED_LINES_ONLY = IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY;

    // IssueFilterConfig
    void testSeverity();

    void testNewOnly();

    void testChangedLinesOnly();

    // ReviewConfig

    void testNoIssuesTitleTemplate();

    void testSomeIssuesTitleTemplate();

    void testIssuesCommentTemplate();

    // ScoreConfig

    void testCategory();

    void testNoIssuesScoreScore();

    void testSomeIssuesScoreScore();

    // NotificationConfig

    void testNoIssuesNotificationRecipient();

    void testIssuesNotificationRecipient();

    void testNegativeScoreNotificationRecipient();

    // Sonar config

    void testSonarUrl();

    void testSonarReportPath();

//    void testProjectConfig();
//
//    void testSonarConfig();

}
