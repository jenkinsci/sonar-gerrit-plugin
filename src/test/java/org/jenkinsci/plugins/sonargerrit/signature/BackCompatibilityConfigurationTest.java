package org.jenkinsci.plugins.sonargerrit.signature;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

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
        Assert.assertNotSame(severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertNull(invokeGetter(p, "scoreConfig")); //
        invokeSetter(p, "severity", severity);

        Assert.assertEquals(severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertNull(invokeGetter(p, "scoreConfig"));

        severity = "CRITICAL";
        Assert.assertNotSame(severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
        invokeSetter(p, "postScore", true);
        invokeSetter(p, "severity", severity);
        Assert.assertEquals(severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertEquals(severity, invokeGetter(p, "scoreConfig", "issueFilterConfig", "severity"));
    }

    @Test
    public void testSetChangedLinesOnly() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean changedLinesOnly = true;
        Assert.assertNotSame(changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertNull(invokeGetter(p, "scoreConfig")); //
        invokeSetter(p, "changedLinesOnly", changedLinesOnly);
        Assert.assertEquals(changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertNull(invokeGetter(p, "scoreConfig"));

        changedLinesOnly = false;
        Assert.assertNotSame(changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        invokeSetter(p, "postScore", true);
        invokeSetter(p, "changedLinesOnly", changedLinesOnly);
        Assert.assertEquals(changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertEquals(changedLinesOnly, invokeGetter(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly")); //
    }

    @Test
    public void testSetNewIssuesOnly() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean newIssuesOnly = true;
        Assert.assertNotSame(newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertNull(invokeGetter(p, "scoreConfig")); //
        invokeSetter(p, "newIssuesOnly", newIssuesOnly);
        Assert.assertEquals(newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertNull(invokeGetter(p, "scoreConfig"));

        newIssuesOnly = false;
        Assert.assertNotSame(newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        invokeSetter(p, "postScore", true);
        invokeSetter(p, "newIssuesOnly", newIssuesOnly);
        Assert.assertEquals(newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertEquals(newIssuesOnly, invokeGetter(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly")); //
    }

    @Test
    public void testSetNoIssuesToPostText() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesToPostText = "Test";
        Assert.assertNotSame(noIssuesToPostText, invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
        invokeSetter(p, "noIssuesToPostText", noIssuesToPostText);
        Assert.assertEquals(noIssuesToPostText, invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
    }

    @Test
    public void testSetSomeIssuesToPostText() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String someIssuesToPostText = "Test";
        Assert.assertNotSame(someIssuesToPostText, invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
        invokeSetter(p, "someIssuesToPostText", someIssuesToPostText);
        Assert.assertEquals(someIssuesToPostText, invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
    }

    @Test
    public void testSetIssueComment() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issueComment = "Test";
        Assert.assertNotSame(issueComment, invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
        invokeSetter(p, "issueComment", issueComment);
        Assert.assertEquals(issueComment, invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
    }

    @Test
    public void testSetOverrideCredentials() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean overrideCredentials = true;
        Assert.assertNull(invokeGetter(p, "authConfig"));
        invokeSetter(p, "overrideCredentials", overrideCredentials);
        Assert.assertNotNull(invokeGetter(p, "authConfig"));

        // todo check false
    }

    @Test
    public void testSetHttpUsername() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String httpUsername = "Test";
        Assert.assertNull(invokeGetter(p, "authConfig"));
        invokeSetter(p, "httpUsername", httpUsername);
        Assert.assertNull(invokeGetter(p, "authConfig"));

        invokeSetter(p, "overrideCredentials", true);
        invokeSetter(p, "httpUsername", httpUsername);
        Assert.assertNotNull(invokeGetter(p, "authConfig"));
        Assert.assertEquals(httpUsername, invokeGetter(p, "authConfig", "username"));
    }

    @Test
    public void testSetHttpPassword() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String httpPassword = "Test";
        Assert.assertNull(invokeGetter(p, "authConfig"));
        invokeSetter(p, "httpPassword", httpPassword);
        Assert.assertNull(invokeGetter(p, "authConfig"));

        invokeSetter(p, "overrideCredentials", true);
        invokeSetter(p, "httpPassword", httpPassword);
        Assert.assertNotNull(invokeGetter(p, "authConfig"));
        Assert.assertEquals(httpPassword, invokeGetter(p, "authConfig", "password"));
    }

    @Test
    public void testSetPostScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        boolean postScore = true;
        Assert.assertNull(invokeGetter(p, "scoreConfig"));
        invokeSetter(p, "postScore", postScore);
        Assert.assertNotNull(invokeGetter(p, "scoreConfig"));
    }

    @Test
    public void testSetCategory() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String category = "Test";
        Assert.assertNull(invokeGetter(p, "scoreConfig"));
        invokeSetter(p, "category", category);
        Assert.assertNull(invokeGetter(p, "scoreConfig"));

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "category", category);
        Assert.assertNotNull(invokeGetter(p, "scoreConfig"));
        Assert.assertEquals(category, invokeGetter(p, "scoreConfig", "category"));
    }

    @Test
    public void testSetNoIssuesScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesScore = "2";
        Assert.assertNull(invokeGetter(p, "scoreConfig"));
        invokeSetter(p, "noIssuesScore", noIssuesScore);
        Assert.assertNull(invokeGetter(p, "scoreConfig"));

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "noIssuesScore", noIssuesScore);
        Assert.assertNotNull(invokeGetter(p, "scoreConfig"));
        Assert.assertEquals(noIssuesScore, invokeGetter(p, "scoreConfig", "noIssuesScore").toString());
    }

    @Test
    public void testSetIssuesScore() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issuesScore = "-2";
        Assert.assertNull(invokeGetter(p, "scoreConfig"));
        invokeSetter(p, "issuesScore", issuesScore);
        Assert.assertNull(invokeGetter(p, "scoreConfig"));

        invokeSetter(p, "postScore", true);
        invokeSetter(p, "issuesScore", issuesScore);
        Assert.assertNotNull(invokeGetter(p, "scoreConfig"));
        Assert.assertEquals(issuesScore, invokeGetter(p, "scoreConfig", "issuesScore").toString());
    }

    @Test
    public void testSetNoIssuesNotification() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String noIssuesNotification = "ALL";
        Assert.assertNotSame(noIssuesNotification, invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
        invokeSetter(p, "noIssuesNotification", noIssuesNotification);
        Assert.assertEquals(noIssuesNotification, invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
    }

    @Test
    public void testSetIssuesNotification() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String issuesNotification = "ALL";
        Assert.assertNotSame(issuesNotification, invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
        invokeSetter(p, "issuesNotification", issuesNotification);
        Assert.assertEquals(issuesNotification, invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
    }

    @Test
    public void testSetProjectPath() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String path = "Test";
        Assert.assertNotSame(path, invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
        invokeSetter(p, "projectPath", path);
        Assert.assertEquals(path, invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    }

    @Test
    public void testSetPath() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String path = "Test";
        Assert.assertNotSame(path, invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
        invokeSetter(p, "path", path);
        Assert.assertEquals(path, invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
    }

    @Test
    public void testSetSonarURL() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String sonarURL = "test";
        Assert.assertNotSame(sonarURL, invokeGetter(p, "inspectionConfig", "serverURL"));
        invokeSetter(p, "sonarURL", "test");
        Assert.assertEquals("test", invokeGetter(p, "inspectionConfig", "serverURL"));
    }

    @Test
    public void testSubJobConfig() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();

        String className = "org.jenkinsci.plugins.sonargerrit.config.SubJobConfig";
        String paramsType = "java.lang.String";
        String[] paramClasses = {paramsType, paramsType};
        String[] params = {"TEST", "TEST"};

        Object c = invokeConstructor(className, paramClasses, params);
        Assert.assertNotSame(invokeGetter(c, "projectPath"), invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
        Assert.assertNotSame(invokeGetter(c, "sonarReportPath"), invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

        LinkedList<Object> value = new LinkedList<>();
        value.add(c);
        invokeSetter(p, "subJobConfigs", value);
        Assert.assertEquals(invokeGetter(c, "projectPath"), invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
        Assert.assertEquals(invokeGetter(c, "sonarReportPath"), invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

    }

    @Override
    protected void invokeSetter(SonarToGerritPublisher obj, String field, Object value) throws ReflectiveOperationException {
        super.invokeSetter(obj, field, value, true);
    }
}
