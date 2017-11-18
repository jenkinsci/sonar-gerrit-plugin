package org.jenkinsci.plugins.sonargerrit.signature;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 17.11.2017 20:54
 * <p/>
 * $Id$
 */
public class BackCompatibilityConfigurationTest extends ConfigurationUpdateTest {
    @Test
    public void testSetSonarURL() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String sonarURL = "test";
        Assert.assertNotSame(sonarURL, p.getSonarURL());
        invokeSetter(p, "sonarURL", "test");
        Assert.assertEquals("test", p.getSonarURL());
        Assert.assertEquals("test", readFieldValue(p, "sonarURL"));
    }

    @Test
    public void testSetSeverity() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String severity = "BLOCKER";
        Assert.assertNotSame(severity, p.getReviewConfig().getIssueFilterConfig().getSeverity());
        Assert.assertNotSame(severity, readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));
        invokeSetter(p, "severity", severity);

        Assert.assertEquals(severity, p.getReviewConfig().getIssueFilterConfig().getSeverity());
        Assert.assertEquals(severity, readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));

        Assert.assertEquals(severity, p.getScoreConfig().getIssueFilterConfig().getSeverity());
    }

    @Test
    public void testSetChangedLinesOnly() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean changedLinesOnly = true;
        Assert.assertTrue(changedLinesOnly != p.getReviewConfig().getIssueFilterConfig().isChangedLinesOnly());
        invokeSetter(p, "changedLinesOnly", changedLinesOnly);
        Assert.assertTrue(changedLinesOnly == p.getReviewConfig().getIssueFilterConfig().isChangedLinesOnly());
        Assert.assertTrue(changedLinesOnly == p.getReviewConfig().getIssueFilterConfig().isChangedLinesOnly());
    }

    @Test
    public void testSetNewIssuesOnly() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean newIssuesOnly = true;
        Assert.assertTrue(newIssuesOnly != p.getReviewConfig().getIssueFilterConfig().isNewIssuesOnly());
        invokeSetter(p, "newIssuesOnly", newIssuesOnly);
        Assert.assertTrue(newIssuesOnly == p.getReviewConfig().getIssueFilterConfig().isNewIssuesOnly());
        Assert.assertTrue(newIssuesOnly == p.getReviewConfig().getIssueFilterConfig().isNewIssuesOnly());
    }

    @Test
    public void testSetNoIssuesToPostText() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesToPostText = "Test";
        Assert.assertNotSame(noIssuesToPostText, p.getReviewConfig().getNoIssuesTitleTemplate());
        invokeSetter(p, "noIssuesToPostText", noIssuesToPostText);
        Assert.assertEquals(noIssuesToPostText, p.getReviewConfig().getNoIssuesTitleTemplate());
        Assert.assertEquals(noIssuesToPostText, p.getReviewConfig().getNoIssuesTitleTemplate());
    }

    @Test
    public void testSetSomeIssuesToPostText() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String someIssuesToPostText = "Test";
        Assert.assertNotSame(someIssuesToPostText, p.getReviewConfig().getSomeIssuesTitleTemplate());
        invokeSetter(p, "someIssuesToPostText", someIssuesToPostText);
        Assert.assertEquals(someIssuesToPostText, p.getReviewConfig().getSomeIssuesTitleTemplate());
        Assert.assertEquals(someIssuesToPostText, p.getReviewConfig().getSomeIssuesTitleTemplate());
    }

    @Test
    public void testSetIssueComment() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issueComment = "Test";
        Assert.assertNotSame(issueComment, p.getReviewConfig().getIssueCommentTemplate());
        invokeSetter(p, "issueComment", issueComment);
        Assert.assertEquals(issueComment, p.getReviewConfig().getIssueCommentTemplate());
        Assert.assertEquals(issueComment, p.getReviewConfig().getIssueCommentTemplate());
    }

    @Test
    public void testSetOverrideCredentials() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean overrideCredentials = true;
        Assert.assertNull(p.getAuthConfig());
        invokeSetter(p, "overrideCredentials", overrideCredentials);
        Assert.assertNotNull(p.getAuthConfig());
    }

    @Test
    public void testSetHttpUsername() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String httpUsername = "Test";
        Assert.assertNull(p.getAuthConfig());
        invokeSetter(p, "httpUsername", httpUsername);
        Assert.assertNull(p.getAuthConfig());

        invokeSetter(p, "overrideCredentials", true);
        invokeSetter(p, "httpUsername", httpUsername);
        Assert.assertNotNull(p.getAuthConfig());
        Assert.assertEquals(httpUsername, p.getAuthConfig().getUsername());
    }

    @Test
    public void testSetHttpPassword() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String httpPassword = "Test";
        Assert.assertNull(p.getAuthConfig());
        invokeSetter(p, "httpPassword", httpPassword);
        Assert.assertNull(p.getAuthConfig());

        invokeSetter(p, "overrideCredentials", true);
        invokeSetter(p, "httpPassword", httpPassword);
        Assert.assertNotNull(p.getAuthConfig());
        Assert.assertEquals(httpPassword, p.getAuthConfig().getPassword());
    }

    @Test
    public void testSetPostScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean postScore = true;
        Assert.assertNull(p.getScoreConfig());
        invokeSetter(p, "postScore", postScore);
        Assert.assertNotNull(p.getScoreConfig());
    }

    @Test
    public void testSetCategory() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String category = "Test";
        Assert.assertNull(p.getScoreConfig());
        invokeSetter(p, "category", category);
        Assert.assertNull(p.getScoreConfig());

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "category", category);
        Assert.assertNotNull(p.getScoreConfig());
        Assert.assertEquals(category, p.getScoreConfig().getCategory());
    }

    @Test
    public void testSetNoIssuesScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesScore = "2";
        Assert.assertNull(p.getScoreConfig());
        invokeSetter(p, "noIssuesScore", noIssuesScore);
        Assert.assertNull(p.getScoreConfig());

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "noIssuesScore", noIssuesScore);
        Assert.assertNotNull(p.getScoreConfig());
        Assert.assertEquals(noIssuesScore, String.valueOf(p.getScoreConfig().getNoIssuesScore()));
    }

    @Test
    public void testSetIssuesScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issuesScore = "-2";
        Assert.assertNull(p.getScoreConfig());
        invokeSetter(p, "issuesScore", issuesScore);
        Assert.assertNull(p.getScoreConfig());

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "issuesScore", issuesScore);
        Assert.assertNotNull(p.getScoreConfig());
        Assert.assertEquals(issuesScore, String.valueOf(p.getScoreConfig().getIssuesScore()));
    }

    @Test
    public void testSetNoIssuesNotification() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesNotification = "ALL";
        Assert.assertNotSame(noIssuesNotification, p.getNotificationConfig().getNoIssuesNotificationRecipient());
        invokeSetter(p, "noIssuesNotification", noIssuesNotification);
        Assert.assertEquals(noIssuesNotification, p.getNotificationConfig().getNoIssuesNotificationRecipient());
    }

    @Test
    public void testSetIssuesNotification() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issuesNotification = "ALL";
        Assert.assertNotSame(issuesNotification, p.getNotificationConfig().getCommentedIssuesNotificationRecipient());
        invokeSetter(p, "issuesNotification", issuesNotification);
        Assert.assertEquals(issuesNotification, p.getNotificationConfig().getCommentedIssuesNotificationRecipient());
    }

    @Test
    public void testSetProjectPath() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String path = "Test";
        Assert.assertNotSame(path, p.getSubJobConfigs().get(0).getProjectPath());
        invokeSetter(p, "projectPath", path);
        Assert.assertEquals(path, p.getSubJobConfigs().get(0).getProjectPath());
    }

    @Test
    public void testSetPath() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String path = "Test";
        Assert.assertNotSame(path, p.getSubJobConfigs().get(0).getSonarReportPath());
        invokeSetter(p, "path", path);
        Assert.assertEquals(path, p.getSubJobConfigs().get(0).getSonarReportPath());
    }
}
