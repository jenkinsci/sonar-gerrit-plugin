package org.jenkinsci.plugins.sonargerrit.signature;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 17.11.2017 20:54
 * $Id$
 */

/*
 * This class checks if it is still possible to keep configuration settings from previous plugin version
 * Methods left for back compatibility purposes should be @Deprecated
 */
public class BackCompatibilityConfigurationTest extends ConfigurationUpdateTest {

    @Test
    public void testSetSeverity() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String severity = "BLOCKER";
        Assert.assertNotSame(severity, readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertNull(readFieldValue(p, "scoreConfig")); //
        invokeSetter(p, "severity", severity);

        Assert.assertEquals(severity, readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertNull(readFieldValue(p, "scoreConfig"));

        severity = "CRITICAL";
        Assert.assertNotSame(severity, readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));
        invokeSetter(p, "postScore", true);
        invokeSetter(p, "severity", severity);
        Assert.assertEquals(severity, readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertEquals(severity, readFieldValue(p, "scoreConfig", "issueFilterConfig", "severity"));
    }

    @Test
    public void testSetChangedLinesOnly() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean changedLinesOnly = true;
        Assert.assertNotSame(changedLinesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertNull(readFieldValue(p, "scoreConfig")); //
        invokeSetter(p, "changedLinesOnly", changedLinesOnly);
        Assert.assertEquals(changedLinesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertNull(readFieldValue(p, "scoreConfig"));

        changedLinesOnly = false;
        Assert.assertNotSame(changedLinesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        invokeSetter(p, "postScore", true);
        invokeSetter(p, "changedLinesOnly", changedLinesOnly);
        Assert.assertEquals(changedLinesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertEquals(changedLinesOnly, readFieldValue(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly")); //
    }

    @Test
    public void testSetNewIssuesOnly() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean newIssuesOnly = true;
        Assert.assertNotSame(newIssuesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertNull(readFieldValue(p, "scoreConfig")); //
        invokeSetter(p, "newIssuesOnly", newIssuesOnly);
        Assert.assertEquals(newIssuesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertNull(readFieldValue(p, "scoreConfig"));

        newIssuesOnly = false;
        Assert.assertNotSame(newIssuesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        invokeSetter(p, "postScore", true);
        invokeSetter(p, "newIssuesOnly", newIssuesOnly);
        Assert.assertEquals(newIssuesOnly, readFieldValue(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertEquals(newIssuesOnly, readFieldValue(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly")); //
    }

    @Test
    public void testSetNoIssuesToPostText() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesToPostText = "Test";
        Assert.assertNotSame(noIssuesToPostText, readFieldValue(p, "reviewConfig", "noIssuesTitleTemplate"));
        invokeSetter(p, "noIssuesToPostText", noIssuesToPostText);
        Assert.assertEquals(noIssuesToPostText, readFieldValue(p, "reviewConfig", "noIssuesTitleTemplate"));
    }

    @Test
    public void testSetSomeIssuesToPostText() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String someIssuesToPostText = "Test";
        Assert.assertNotSame(someIssuesToPostText, readFieldValue(p, "reviewConfig", "someIssuesTitleTemplate"));
        invokeSetter(p, "someIssuesToPostText", someIssuesToPostText);
        Assert.assertEquals(someIssuesToPostText, readFieldValue(p, "reviewConfig", "someIssuesTitleTemplate"));
    }

    @Test
    public void testSetIssueComment() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issueComment = "Test";
        Assert.assertNotSame(issueComment, readFieldValue(p, "reviewConfig", "issueCommentTemplate"));
        invokeSetter(p, "issueComment", issueComment);
        Assert.assertEquals(issueComment, readFieldValue(p, "reviewConfig", "issueCommentTemplate"));
    }

    @Test
    public void testSetOverrideCredentials() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean overrideCredentials = true;
        Assert.assertNull(readFieldValue(p, "authConfig"));
        invokeSetter(p, "overrideCredentials", overrideCredentials);
        Assert.assertNotNull(readFieldValue(p, "authConfig"));

        // todo check false
    }

    @Test
    public void testSetHttpUsername() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String httpUsername = "Test";
        Assert.assertNull(readFieldValue(p, "authConfig"));
        invokeSetter(p, "httpUsername", httpUsername);
        Assert.assertNull(readFieldValue(p, "authConfig"));

        invokeSetter(p, "overrideCredentials", true);
        invokeSetter(p, "httpUsername", httpUsername);
        Assert.assertNotNull(readFieldValue(p, "authConfig"));
        Assert.assertEquals(httpUsername, readFieldValue(p, "authConfig", "username"));
    }

    @Test
    public void testSetHttpPassword() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String httpPassword = "Test";
        Assert.assertNull(readFieldValue(p, "authConfig"));
        invokeSetter(p, "httpPassword", httpPassword);
        Assert.assertNull(readFieldValue(p, "authConfig"));

        invokeSetter(p, "overrideCredentials", true);
        invokeSetter(p, "httpPassword", httpPassword);
        Assert.assertNotNull(readFieldValue(p, "authConfig"));
        Assert.assertEquals(httpPassword, readFieldValue(p, "authConfig", "password"));
    }

    @Test
    public void testSetPostScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean postScore = true;
        Assert.assertNull(readFieldValue(p, "scoreConfig"));
        invokeSetter(p, "postScore", postScore);
        Assert.assertNotNull(readFieldValue(p, "scoreConfig"));
    }

    @Test
    public void testSetCategory() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String category = "Test";
        Assert.assertNull(readFieldValue(p, "scoreConfig"));
        invokeSetter(p, "category", category);
        Assert.assertNull(readFieldValue(p, "scoreConfig"));

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "category", category);
        Assert.assertNotNull(readFieldValue(p, "scoreConfig"));
        Assert.assertEquals(category, readFieldValue(p, "scoreConfig", "category"));
    }

    @Test
    public void testSetNoIssuesScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesScore = "2";
        Assert.assertNull(readFieldValue(p, "scoreConfig"));
        invokeSetter(p, "noIssuesScore", noIssuesScore);
        Assert.assertNull(readFieldValue(p, "scoreConfig"));

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "noIssuesScore", noIssuesScore);
        Assert.assertNotNull(readFieldValue(p, "scoreConfig"));
        Assert.assertEquals(noIssuesScore, readFieldValue(p, "scoreConfig", "noIssuesScore").toString());
    }

    @Test
    public void testSetIssuesScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issuesScore = "-2";
        Assert.assertNull(readFieldValue(p, "scoreConfig"));
        invokeSetter(p, "issuesScore", issuesScore);
        Assert.assertNull(readFieldValue(p, "scoreConfig"));

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "issuesScore", issuesScore);
        Assert.assertNotNull(readFieldValue(p, "scoreConfig"));
        Assert.assertEquals(issuesScore, readFieldValue(p, "scoreConfig", "issuesScore").toString());
    }

    @Test
    public void testSetNoIssuesNotification() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesNotification = "ALL";
        Assert.assertNotSame(noIssuesNotification, readFieldValue(p, "notificationConfig", "noIssuesNotificationRecipient"));
        invokeSetter(p, "noIssuesNotification", noIssuesNotification);
        Assert.assertEquals(noIssuesNotification, readFieldValue(p, "notificationConfig", "noIssuesNotificationRecipient"));
    }

    @Test
    public void testSetIssuesNotification() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issuesNotification = "ALL";
        Assert.assertNotSame(issuesNotification, readFieldValue(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
        invokeSetter(p, "issuesNotification", issuesNotification);
        Assert.assertEquals(issuesNotification, readFieldValue(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
    }

    @Test
    public void testSetProjectPath() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String path = "Test";
        Assert.assertNotSame(path, readFieldValue(p, "inspectionConfig", "baseConfig", "projectPath"));
        invokeSetter(p, "projectPath", path);
        Assert.assertEquals(path, readFieldValue(p, "inspectionConfig", "baseConfig", "projectPath"));
    }

    @Test
    public void testSetPath() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String path = "Test";
        Assert.assertNotSame(path, readFieldValue(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
        invokeSetter(p, "path", path);
        Assert.assertEquals(path, readFieldValue(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
    }

    @Override
    protected void invokeSetter(SonarToGerritPublisher obj, String field, Object value) throws ReflectiveOperationException {
        super.invokeSetter(obj, field, value, true);
    }
}
