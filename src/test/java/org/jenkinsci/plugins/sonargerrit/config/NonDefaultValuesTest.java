package org.jenkinsci.plugins.sonargerrit.config;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 21:29
 *
 * <p>$Id$
 */
public class NonDefaultValuesTest implements DetailedConfigTest {

  @Test
  public void testSeverity() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assert.assertEquals(SEVERITY, config.getSeverity());
    Assert.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assert.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    Assert.assertNotSame("CRITICAL", config.getSeverity());
    config.setSeverity("CRITICAL");
    Assert.assertEquals("CRITICAL", config.getSeverity());
    Assert.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assert.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());
  }

  @Test
  public void testNewOnly() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assert.assertEquals(SEVERITY, config.getSeverity());
    Assert.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assert.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    boolean anew = !config.isNewIssuesOnly();
    config.setNewIssuesOnly(anew);
    Assert.assertEquals(SEVERITY, config.getSeverity());
    Assert.assertEquals(anew, config.isNewIssuesOnly());
    Assert.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());
  }

  @Test
  public void testChangedLinesOnly() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assert.assertEquals(SEVERITY, config.getSeverity());
    Assert.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assert.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    boolean changed = !config.isChangedLinesOnly();
    config.setChangedLinesOnly(changed);
    Assert.assertEquals(SEVERITY, config.getSeverity());
    Assert.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assert.assertEquals(changed, config.isChangedLinesOnly());
  }

  @Test
  public void testFilterConfig() {
    IssueFilterConfig config = new IssueFilterConfig();
    Assert.assertEquals(SEVERITY, config.getSeverity());
    Assert.assertEquals(NEW_ISSUES_ONLY, config.isNewIssuesOnly());
    Assert.assertEquals(CHANGED_LINES_ONLY, config.isChangedLinesOnly());

    Assert.assertNotSame("CRITICAL", config.getSeverity());
    boolean anew = !config.isNewIssuesOnly();
    boolean changed = !config.isChangedLinesOnly();

    config.setSeverity("CRITICAL");
    config.setNewIssuesOnly(anew);
    config.setChangedLinesOnly(changed);

    Assert.assertEquals("CRITICAL", config.getSeverity());
    Assert.assertEquals(anew, config.isNewIssuesOnly());
    Assert.assertEquals(changed, config.isChangedLinesOnly());
  }

  @Test
  public void testNoIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assert.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assert.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assert.assertNotSame("Test", config.getNoIssuesTitleTemplate());
    config.setNoIssuesTitleTemplate("Test");
    Assert.assertEquals("Test", config.getNoIssuesTitleTemplate());
    Assert.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assert.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Test
  public void testSomeIssuesTitleTemplate() {
    ReviewConfig config = new ReviewConfig();
    Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assert.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assert.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assert.assertNotSame("Test", config.getSomeIssuesTitleTemplate());
    config.setSomeIssuesTitleTemplate("Test");
    Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assert.assertEquals("Test", config.getSomeIssuesTitleTemplate());
    Assert.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
  }

  @Test
  public void testIssuesCommentTemplate() {
    ReviewConfig config = new ReviewConfig();
    Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assert.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assert.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    Assert.assertNotSame("Test", config.getIssueCommentTemplate());
    config.setIssueCommentTemplate("Test");
    Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assert.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assert.assertEquals("Test", config.getIssueCommentTemplate());
  }

  @Test
  public void testReviewConfig() {
    ReviewConfig config = new ReviewConfig();
    Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    Assert.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    Assert.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());

    Assert.assertNotSame("Test1", config.getNoIssuesTitleTemplate());
    Assert.assertNotSame("Test2", config.getSomeIssuesTitleTemplate());
    Assert.assertNotSame("Test3", config.getIssueCommentTemplate());

    config.setNoIssuesTitleTemplate("Test1");
    config.setSomeIssuesTitleTemplate("Test2");
    config.setIssueCommentTemplate("Test3");

    Assert.assertEquals("Test1", config.getNoIssuesTitleTemplate());
    Assert.assertEquals("Test2", config.getSomeIssuesTitleTemplate());
    Assert.assertEquals("Test3", config.getIssueCommentTemplate());
  }

  @Test
  public void testCategory() {
    ScoreConfig config = new ScoreConfig();
    Assert.assertEquals(CATEGORY, config.getCategory());
    Assert.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assert.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assert.assertNotSame("Test", CATEGORY);
    config.setCategory("Test");

    Assert.assertEquals("Test", config.getCategory());
    Assert.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assert.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());
  }

  @Override
  public void testNoIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    Assert.assertEquals(CATEGORY, config.getCategory());
    Assert.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assert.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assert.assertNotSame(2, NO_ISSUES_SCORE);
    config.setNoIssuesScore(2);

    Assert.assertEquals(CATEGORY, config.getCategory());
    Assert.assertEquals(Integer.valueOf(2), config.getNoIssuesScore());
    Assert.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());
  }

  @Test
  public void testSomeIssuesScoreScore() {
    ScoreConfig config = new ScoreConfig();
    Assert.assertEquals(CATEGORY, config.getCategory());
    Assert.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assert.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assert.assertNotSame(-2, SOME_ISSUES_SCORE);
    config.setIssuesScore(-2);

    Assert.assertEquals(CATEGORY, config.getCategory());
    Assert.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assert.assertEquals(Integer.valueOf(-2), config.getIssuesScore());
  }

  @Test
  public void testScoreConfig() {
    ScoreConfig config = new ScoreConfig();
    Assert.assertEquals(CATEGORY, config.getCategory());
    Assert.assertEquals(NO_ISSUES_SCORE, config.getNoIssuesScore());
    Assert.assertEquals(SOME_ISSUES_SCORE, config.getIssuesScore());

    Assert.assertNotSame("Testo", CATEGORY);
    Assert.assertNotSame(3, NO_ISSUES_SCORE);
    Assert.assertNotSame(-3, SOME_ISSUES_SCORE);

    config.setCategory("Testo");
    config.setNoIssuesScore(3);
    config.setIssuesScore(-3);

    Assert.assertEquals("Testo", config.getCategory());
    Assert.assertEquals(Integer.valueOf(3), config.getNoIssuesScore());
    Assert.assertEquals(Integer.valueOf(-3), config.getIssuesScore());
  }

  @Test
  public void testNoIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    Assert.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assert.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assert.assertNotSame("ALL", NO_ISSUES_NOTIFICATION);
    config.setNoIssuesNotificationRecipient("ALL");

    Assert.assertEquals("ALL", config.getNoIssuesNotificationRecipient());
    Assert.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Test
  public void testIssuesNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    Assert.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assert.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assert.assertNotSame("ALL", ISSUES_NOTIFICATION);
    config.setCommentedIssuesNotificationRecipient("ALL");

    Assert.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assert.assertEquals("ALL", config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
  }

  @Test
  public void testNegativeScoreNotificationRecipient() {
    NotificationConfig config = new NotificationConfig();
    Assert.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assert.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assert.assertNotSame("ALL", SCORE_NOTIFICATION);
    config.setNegativeScoreNotificationRecipient("ALL");

    Assert.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assert.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals("ALL", config.getNegativeScoreNotificationRecipient());
  }

  @Test
  public void testNotificationConfig() {
    NotificationConfig config = new NotificationConfig();
    Assert.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    Assert.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());

    Assert.assertNotSame("OWNER", NO_ISSUES_NOTIFICATION);
    Assert.assertNotSame("OWNER_REVIEWER", ISSUES_NOTIFICATION);
    Assert.assertNotSame("NONE", SCORE_NOTIFICATION);

    config.setNoIssuesNotificationRecipient("OWNER");
    config.setCommentedIssuesNotificationRecipient("OWNER_REVIEWERS");
    config.setNegativeScoreNotificationRecipient("NONE");

    Assert.assertEquals("OWNER", config.getNoIssuesNotificationRecipient());
    Assert.assertEquals("OWNER_REVIEWERS", config.getCommentedIssuesNotificationRecipient());
    Assert.assertEquals("NONE", config.getNegativeScoreNotificationRecipient());
  }

  @Test
  public void testUsername() {
    GerritAuthenticationConfig config = new GerritAuthenticationConfig();
    Assert.assertNull(config.getUsername());
    Assert.assertNull(config.getPassword());

    config.setUsername("Test");

    Assert.assertEquals("Test", config.getUsername());
    Assert.assertNull(config.getPassword());
  }

  @Test
  public void testAuthenticationConfig() {
    GerritAuthenticationConfig config = new GerritAuthenticationConfig();
    Assert.assertNull(config.getUsername());
    Assert.assertNull(config.getPassword());

    config.setUsername("TestUsr");

    Assert.assertEquals("TestUsr", config.getUsername());
  }

  @Test
  public void testSonarUrl() {
    InspectionConfig config = new InspectionConfig();
    Assert.assertEquals(SONAR_URL, config.getServerURL());
    Assert.assertNotSame("Test", SONAR_URL);
    config.setServerURL("Test");
    Assert.assertEquals("Test", config.getServerURL());
  }

  @Test
  public void testSonarReportPath() {
    SubJobConfig config = new SubJobConfig();
    Assert.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
    Assert.assertNotSame("Test", SONAR_REPORT_PATH);
    config.setSonarReportPath("Test");
    Assert.assertEquals("Test", config.getSonarReportPath());
  }

  @Test
  public void testProjectConfig() {
    SubJobConfig config = new SubJobConfig();
    Assert.assertEquals(PROJECT_PATH, config.getProjectPath());
    Assert.assertNotSame("Test", PROJECT_PATH);
    config.setProjectPath("Test");
    Assert.assertEquals("Test", config.getProjectPath());
  }

  @Test
  public void testInspectionConfig() {
    InspectionConfig config = new InspectionConfig();
    Assert.assertEquals(SONAR_URL, config.getServerURL());
    Assert.assertEquals(SONAR_REPORT_PATH, config.getBaseConfig().getSonarReportPath());
    Assert.assertEquals(PROJECT_PATH, config.getBaseConfig().getProjectPath());
    Assert.assertEquals(PATH_AUTO_MATCH, config.getBaseConfig().isAutoMatch());
    Assert.assertTrue(config.isType(DEFAULT_INSPECTION_CONFIG_TYPE));
    Assert.assertEquals(1, config.getSubJobConfigs().size());
    SubJobConfig subJobConfig = new ArrayList<>(config.getSubJobConfigs()).get(0);
    Assert.assertEquals(SONAR_REPORT_PATH, subJobConfig.getSonarReportPath());
    Assert.assertEquals(PROJECT_PATH, subJobConfig.getProjectPath());
    Assert.assertEquals(PATH_AUTO_MATCH, subJobConfig.isAutoMatch());
    Assert.assertEquals(config.isAutoMatch(), config.getBaseConfig().isAutoMatch());
    Assert.assertFalse(config.isMultiConfigMode());
    Assert.assertEquals(
        config.getBaseConfig(), new ArrayList<>(config.getAllSubJobConfigs()).get(0));

    Assert.assertNotSame("Test1", SONAR_URL);
    Assert.assertNotSame("Test2", SONAR_REPORT_PATH);
    Assert.assertNotSame("Test3", PROJECT_PATH);
    Assert.assertNotSame(true, PATH_AUTO_MATCH);
    Assert.assertNotSame("multi", DEFAULT_INSPECTION_CONFIG_TYPE);

    config.setServerURL("Test1");
    config.getBaseConfig().setSonarReportPath("Test2");
    config.getBaseConfig().setProjectPath("Test3");

    config.setAutoMatch(true);
    Assert.assertTrue(config.getBaseConfig().isAutoMatch());
    Assert.assertEquals(config.isAutoMatch(), config.getBaseConfig().isAutoMatch());
    config.setType("multi");
    Assert.assertNotSame(config.isAutoMatch(), config.getBaseConfig().isAutoMatch());

    Assert.assertEquals("Test1", config.getServerURL());
    Assert.assertEquals("Test2", config.getBaseConfig().getSonarReportPath());
    Assert.assertEquals("Test3", config.getBaseConfig().getProjectPath());
    Assert.assertTrue(config.isMultiConfigMode());
    Assert.assertNotSame(
        config.getBaseConfig(), new ArrayList<>(config.getAllSubJobConfigs()).get(0));
    Assert.assertEquals(
        new ArrayList<>(config.getSubJobConfigs()).get(0),
        new ArrayList<>(config.getAllSubJobConfigs()).get(0));
  }

  @Override
  public void testSubJobConfig() {
    SubJobConfig config = new SubJobConfig();
    Assert.assertEquals(SONAR_REPORT_PATH, config.getSonarReportPath());
    Assert.assertEquals(PROJECT_PATH, config.getProjectPath());

    Assert.assertNotSame("Test12", SONAR_REPORT_PATH);
    Assert.assertNotSame("Test13", PROJECT_PATH);

    config.setSonarReportPath("Test12");
    config.setProjectPath("Test13");

    Assert.assertEquals("Test12", config.getSonarReportPath());
    Assert.assertEquals("Test13", config.getProjectPath());
  }
}
