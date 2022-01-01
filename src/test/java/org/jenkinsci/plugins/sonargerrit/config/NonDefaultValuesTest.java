package org.jenkinsci.plugins.sonargerrit.config;

import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 21:29
 *
 * <p>$Id$
 */
public class NonDefaultValuesTest implements DetailedConfigTest {

  @Override
  @Test
  public void testSeverity() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assertions.assertEquals(SEVERITY, config.getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    Assertions.assertNotSame("CRITICAL", config.getSeverity());
    config.setSeverity("CRITICAL");
    Assertions.assertEquals("CRITICAL", config.getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());
  }

  @Override
  @Test
  public void testNewOnly() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assertions.assertEquals(SEVERITY, config.getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    boolean anew = !config.isNewIssuesOnly();
    config.setNewIssuesOnly(anew);
    Assertions.assertEquals(SEVERITY, config.getSeverity());
    Assertions.assertEquals(anew, config.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());
  }

  @Override
  @Test
  public void testChangedLinesOnly() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assertions.assertEquals(SEVERITY, config.getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    boolean changed = !config.isChangedLinesOnly();
    config.setChangedLinesOnly(changed);
    Assertions.assertEquals(SEVERITY, config.getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assertions.assertEquals(changed, config.isChangedLinesOnly());
  }

  @Override
  @Test
  public void testFilterConfig() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assertions.assertEquals(SEVERITY, config.getSeverity());
    Assertions.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assertions.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    Assertions.assertNotSame("CRITICAL", config.getSeverity());
    boolean anew = !config.isNewIssuesOnly();
    boolean changed = !config.isChangedLinesOnly();

    config.setSeverity("CRITICAL");
    config.setNewIssuesOnly(anew);
    config.setChangedLinesOnly(changed);

    Assertions.assertEquals("CRITICAL", config.getSeverity());
    Assertions.assertEquals(anew, config.isNewIssuesOnly());
    Assertions.assertEquals(changed, config.isChangedLinesOnly());
  }

  @Override
  @Test
  public void testNoIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assertions.assertNotSame("Test", config.getNoIssuesTitleTemplate());
    config.setNoIssuesTitleTemplate("Test");
    Assertions.assertEquals("Test", config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Override
  @Test
  public void testSomeIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assertions.assertNotSame("Test", config.getSomeIssuesTitleTemplate());
    config.setSomeIssuesTitleTemplate("Test");
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals("Test", config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Override
  @Test
  public void testIssuesCommentTemplate() {
    ReviewConfig config = new ReviewConfig();
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assertions.assertNotSame("Test", config.getIssueCommentTemplate());
    config.setIssueCommentTemplate("Test");
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals("Test", config.getIssueCommentTemplate());
  }

  @Override
  @Test
  public void testReviewConfig() {
    ReviewConfig config = new ReviewConfig();
    Assertions.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assertions.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());

    Assertions.assertNotSame("Test1", config.getNoIssuesTitleTemplate());
    Assertions.assertNotSame("Test2", config.getSomeIssuesTitleTemplate());
    Assertions.assertNotSame("Test3", config.getIssueCommentTemplate());

    config.setNoIssuesTitleTemplate("Test1");
    config.setSomeIssuesTitleTemplate("Test2");
    config.setIssueCommentTemplate("Test3");

    Assertions.assertEquals("Test1", config.getNoIssuesTitleTemplate());
    Assertions.assertEquals("Test2", config.getSomeIssuesTitleTemplate());
    Assertions.assertEquals("Test3", config.getIssueCommentTemplate());
  }

  @Override
  @Test
  public void testCategory() {
    ScoreConfig config = new ScoreConfig();
    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assertions.assertNotSame("Test", CATEGORY);
    config.setCategory("Test");

    Assertions.assertEquals("Test", config.getCategory());
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());
  }

  @Override
  public void testNoIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assertions.assertNotSame(2, NO_ISSUES_SCORE);
    config.setNoIssuesScore(2);

    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(Integer.valueOf(2), config.getNoIssuesScore());
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());
  }

  @Override
  @Test
  public void testSomeIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assertions.assertNotSame(-2, SOME_ISSUES_SCORE);
    config.setIssuesScore(-2);

    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assertions.assertEquals(Integer.valueOf(-2), config.getIssuesScore());
  }

  @Override
  @Test
  public void testScoreConfig() {
    ScoreConfig config = new ScoreConfig();
    Assertions.assertEquals(CATEGORY, config.getCategory());
    Assertions.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assertions.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assertions.assertNotSame("Testo", CATEGORY);
    Assertions.assertNotSame(3, NO_ISSUES_SCORE);
    Assertions.assertNotSame(-3, SOME_ISSUES_SCORE);

    config.setCategory("Testo");
    config.setNoIssuesScore(3);
    config.setIssuesScore(-3);

    Assertions.assertEquals("Testo", config.getCategory());
    Assertions.assertEquals(Integer.valueOf(3), config.getNoIssuesScore());
    Assertions.assertEquals(Integer.valueOf(-3), config.getIssuesScore());
  }

  @Override
  @Test
  public void testNoIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assertions.assertNotSame("ALL", NO_ISSUES_NOTIFICATION);
    config.setNoIssuesNotificationRecipient("ALL");

    Assertions.assertEquals("ALL", config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  @Test
  public void testIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assertions.assertNotSame("ALL", ISSUES_NOTIFICATION);
    config.setCommentedIssuesNotificationRecipient("ALL");

    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals("ALL", config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Override
  @Test
  public void testNegativeScoreNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assertions.assertNotSame("ALL", SCORE_NOTIFICATION);
    config.setNegativeScoreNotificationRecipient("ALL");

    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals("ALL", config.getNegativeScoreNotificationRecipient());
  }

  @Override
  @Test
  public void testNotificationConfig() {
    NotificationConfig config = new NotificationConfig();
    Assertions.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assertions.assertNotSame("OWNER", NO_ISSUES_NOTIFICATION);
    Assertions.assertNotSame("OWNER_REVIEWER", ISSUES_NOTIFICATION);
    Assertions.assertNotSame("NONE", SCORE_NOTIFICATION);

    config.setNoIssuesNotificationRecipient("OWNER");
    config.setCommentedIssuesNotificationRecipient("OWNER_REVIEWERS");
    config.setNegativeScoreNotificationRecipient("NONE");

    Assertions.assertEquals("OWNER", config.getNoIssuesNotificationRecipient());
    Assertions.assertEquals("OWNER_REVIEWERS", config.getCommentedIssuesNotificationRecipient());
    Assertions.assertEquals("NONE", config.getNegativeScoreNotificationRecipient());
  }

  @Test
  public void testUsername() {
    GerritAuthenticationConfig config = new GerritAuthenticationConfig();
    Assertions.assertNull(config.getUsername());
    Assertions.assertNull(config.getPassword());

    config.setUsername("Test");

    Assertions.assertEquals("Test", config.getUsername());
    Assertions.assertNull(config.getPassword());
  }

  @Override
  @Test
  public void testAuthenticationConfig() {
    GerritAuthenticationConfig config = new GerritAuthenticationConfig();
    Assertions.assertNull(config.getUsername());
    Assertions.assertNull(config.getPassword());

    config.setUsername("TestUsr");

    Assertions.assertEquals("TestUsr", config.getUsername());
  }

  @Override
  @Test
  public void testSonarUrl() {
    InspectionConfig config = new InspectionConfig();
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
    Assertions.assertNotSame("Test", SONAR_URL);
    config.setServerURL("Test");
    Assertions.assertEquals("Test", config.getServerURL());
  }

  @Override
  @Test
  public void testSonarReportPath() {
    SubJobConfig config = new SubJobConfig();
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
    Assertions.assertNotSame("Test", SONAR_REPORT_PATH);
    config.setSonarReportPath("Test");
    Assertions.assertEquals("Test", config.getSonarReportPath());
  }

  @Override
  @Test
  public void testProjectConfig() {
    SubJobConfig config = new SubJobConfig();
    Assertions.assertEquals(PROJECT_PATH, config.getProjectPath());
    Assertions.assertNotSame("Test", PROJECT_PATH);
    config.setProjectPath("Test");
    Assertions.assertEquals("Test", config.getProjectPath());
  }

  @Override
  @Test
  public void testInspectionConfig() {
    InspectionConfig config = new InspectionConfig();
    Assertions.assertEquals(SONAR_URL, config.getServerURL());
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, config.getBaseConfig().getProjectPath());
    Assertions.assertEquals(PATH_AUTO_MATCH, config.getBaseConfig().isAutoMatch());
    Assertions.assertTrue(config.isType(DEFAULT_INSPECTION_CONFIG_TYPE));
    Assertions.assertEquals(1, config.getSubJobConfigs().size());
    SubJobConfig subJobConfig = new ArrayList<>(config.getSubJobConfigs()).get(0);
    Assertions.assertEquals(SONAR_REPORT_PATH, subJobConfig.getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, subJobConfig.getProjectPath());
    Assertions.assertEquals(PATH_AUTO_MATCH, subJobConfig.isAutoMatch());
    Assertions.assertEquals(config.isAutoMatch(), config.getBaseConfig().isAutoMatch());
    Assertions.assertFalse(config.isMultiConfigMode());
    Assertions.assertEquals(
        config.getBaseConfig(), new ArrayList<>(config.getAllSubJobConfigs()).get(0));

    Assertions.assertNotSame("Test1", SONAR_URL);
    Assertions.assertNotSame("Test2", SONAR_REPORT_PATH);
    Assertions.assertNotSame("Test3", PROJECT_PATH);
    Assertions.assertNotSame(true, PATH_AUTO_MATCH);
    Assertions.assertNotSame("multi", DEFAULT_INSPECTION_CONFIG_TYPE);

    config.setServerURL("Test1");
    config.getBaseConfig().setSonarReportPath("Test2");
    config.getBaseConfig().setProjectPath("Test3");

    config.setAutoMatch(true);
    Assertions.assertTrue(config.getBaseConfig().isAutoMatch());
    Assertions.assertEquals(config.isAutoMatch(), config.getBaseConfig().isAutoMatch());
    config.setType("multi");
    Assertions.assertNotSame(config.isAutoMatch(), config.getBaseConfig().isAutoMatch());

    Assertions.assertEquals("Test1", config.getServerURL());
    Assertions.assertEquals("Test2", config.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals("Test3", config.getBaseConfig().getProjectPath());
    Assertions.assertTrue(config.isMultiConfigMode());
    Assertions.assertNotSame(
        config.getBaseConfig(), new ArrayList<>(config.getAllSubJobConfigs()).get(0));
    Assertions.assertEquals(
        new ArrayList<>(config.getSubJobConfigs()).get(0),
        new ArrayList<>(config.getAllSubJobConfigs()).get(0));
  }

  @Override
  public void testSubJobConfig() {
    SubJobConfig config = new SubJobConfig();
    Assertions.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, config.getProjectPath());

    Assertions.assertNotSame("Test12", SONAR_REPORT_PATH);
    Assertions.assertNotSame("Test13", PROJECT_PATH);

    config.setSonarReportPath("Test12");
    config.setProjectPath("Test13");

    Assertions.assertEquals("Test12", config.getSonarReportPath());
    Assertions.assertEquals("Test13", config.getProjectPath());
  }
}
