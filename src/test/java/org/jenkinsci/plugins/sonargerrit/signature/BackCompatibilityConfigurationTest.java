package org.jenkinsci.plugins.sonargerrit.signature;

import java.util.LinkedList;
import java.util.List;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.stapler.DataBoundSetter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 17.11.2017 20:54 $Id$ */

/*
 * This class checks if it is still possible to keep configuration settings from previous plugin version
 * Methods left for back compatibility purposes should be @Deprecated
 */
public class BackCompatibilityConfigurationTest extends ConfigurationUpdateTest {

  @Test
  public void testSetSeverity() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String severity = "BLOCKER";
    Assert.assertNotSame(
        severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assert.assertNull(invokeGetter(p, "scoreConfig")); //
    invokeSetter(p, "severity", severity);

    Assert.assertEquals(severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assert.assertNull(invokeGetter(p, "scoreConfig"));

    severity = "CRITICAL";
    Assert.assertNotSame(
        severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    invokeSetter(p, "postScore", true);
    invokeSetter(p, "severity", severity);
    Assert.assertEquals(severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assert.assertEquals(severity, invokeGetter(p, "scoreConfig", "issueFilterConfig", "severity"));
  }

  @Test
  public void testSetChangedLinesOnly() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    boolean changedLinesOnly = true;
    Assert.assertNotSame(
        changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assert.assertNull(invokeGetter(p, "scoreConfig")); //
    invokeSetter(p, "changedLinesOnly", changedLinesOnly);
    Assert.assertEquals(
        changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assert.assertNull(invokeGetter(p, "scoreConfig"));

    changedLinesOnly = false;
    Assert.assertNotSame(
        changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    invokeSetter(p, "postScore", true);
    invokeSetter(p, "changedLinesOnly", changedLinesOnly);
    Assert.assertEquals(
        changedLinesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assert.assertEquals(
        changedLinesOnly,
        invokeGetter(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly")); //
  }

  @Test
  public void testSetNewIssuesOnly() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    boolean newIssuesOnly = true;
    Assert.assertNotSame(
        newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assert.assertNull(invokeGetter(p, "scoreConfig")); //
    invokeSetter(p, "newIssuesOnly", newIssuesOnly);
    Assert.assertEquals(
        newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assert.assertNull(invokeGetter(p, "scoreConfig"));

    newIssuesOnly = false;
    Assert.assertNotSame(
        newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    invokeSetter(p, "postScore", true);
    invokeSetter(p, "newIssuesOnly", newIssuesOnly);
    Assert.assertEquals(
        newIssuesOnly, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assert.assertEquals(
        newIssuesOnly, invokeGetter(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly")); //
  }

  @Test
  public void testSetNoIssuesToPostText() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String noIssuesToPostText = "Test";
    Assert.assertNotSame(
        noIssuesToPostText, invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
    invokeSetter(p, "noIssuesToPostText", noIssuesToPostText);
    Assert.assertEquals(
        noIssuesToPostText, invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
  }

  @Test
  public void testSetSomeIssuesToPostText() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String someIssuesToPostText = "Test";
    Assert.assertNotSame(
        someIssuesToPostText, invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
    invokeSetter(p, "someIssuesToPostText", someIssuesToPostText);
    Assert.assertEquals(
        someIssuesToPostText, invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
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
    //        invokeSetter(p, "httpUsername", httpUsername);
    Assert.assertNotNull(invokeGetter(p, "authConfig"));
    Assert.assertEquals(httpUsername, invokeGetter(p, "authConfig", "username"));
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
    //        invokeSetter(p, "category", category);
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
    //        invokeSetter(p, "noIssuesScore", noIssuesScore);
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
    //        invokeSetter(p, "issuesScore", issuesScore);
    Assert.assertNotNull(invokeGetter(p, "scoreConfig"));
    Assert.assertEquals(issuesScore, invokeGetter(p, "scoreConfig", "issuesScore").toString());
  }

  @Test
  public void testGetter() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    Assert.assertNull(invokeGetter(p, true, "severity"));
    Assert.assertFalse((boolean) invokeGetter(p, true, true, "newIssuesOnly"));
    Assert.assertFalse((boolean) invokeGetter(p, true, true, "changedLinesOnly"));
    Assert.assertNull(invokeGetter(p, true, "noIssuesToPostText"));
    Assert.assertNull(invokeGetter(p, true, "someIssuesToPostText"));
    Assert.assertNull(invokeGetter(p, true, "issueComment"));
    Assert.assertFalse((boolean) invokeGetter(p, true, true, "overrideCredentials"));
    Assert.assertNull(invokeGetter(p, true, "httpUsername"));
    Assert.assertNull(invokeGetter(p, true, "httpPassword"));
    Assert.assertFalse((boolean) invokeGetter(p, true, true, "postScore"));
    Assert.assertNull(invokeGetter(p, true, "category"));
    Assert.assertNull(invokeGetter(p, true, "noIssuesScore"));
    Assert.assertNull(invokeGetter(p, true, "issuesScore"));
    Assert.assertNull(invokeGetter(p, true, "subJobConfigs"));
    Assert.assertNull(invokeGetter(p, true, "projectPath"));
    Assert.assertNull(invokeGetter(p, true, "path"));
    Assert.assertNull(invokeGetter(p, true, "noIssuesNotification"));
    Assert.assertNull(invokeGetter(p, true, "issuesNotification"));
  }

  @Test
  public void testSetNoIssuesNotification() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String noIssuesNotification = "ALL";
    Assert.assertNotSame(
        noIssuesNotification,
        invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
    invokeSetter(p, "noIssuesNotification", noIssuesNotification);
    Assert.assertEquals(
        noIssuesNotification,
        invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
  }

  @Test
  public void testSetIssuesNotification() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String issuesNotification = "ALL";
    Assert.assertNotSame(
        issuesNotification,
        invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
    invokeSetter(p, "issuesNotification", issuesNotification);
    Assert.assertEquals(
        issuesNotification,
        invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
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
    Assert.assertNotSame(
        path, invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
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
    Assert.assertNotSame(
        invokeGetter(c, "projectPath"),
        invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Assert.assertNotSame(
        invokeGetter(c, "sonarReportPath"),
        invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

    List value = new LinkedList<>();
    value.add(c);
    //        next string doesn't work because value type is LinkedList, but setter parameter type
    // is List.
    //        invokeSetter(p, "subJobConfigs", value);
    invokeMethod(p, "setSubJobConfigs", value, Deprecated.class, DataBoundSetter.class);
    Assert.assertEquals(
        invokeGetter(c, "projectPath"),
        invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Assert.assertEquals(
        invokeGetter(c, "sonarReportPath"),
        invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
  }

  @Override
  protected void invokeSetter(SonarToGerritPublisher obj, String field, Object value)
      throws ReflectiveOperationException {
    super.invokeSetter(obj, field, value, true);
  }

  @Override
  protected Object invokeGetter(Object obj, String... field) throws ReflectiveOperationException {
    return invokeGetter(obj, false, false, field);
  }

  protected Object invokeGetter(Object obj, boolean deprecated, String... field)
      throws ReflectiveOperationException {
    return invokeGetter(obj, deprecated, false, field);
  }

  protected Object invokeGetter(Object obj, boolean deprecated, boolean isBool, String... field)
      throws ReflectiveOperationException {
    if (deprecated) {
      return invokeDeprecatedGetter(obj, isBool, field[0]);
    } else {
      return super.invokeGetter(obj, field);
    }
  }

  private Object invokeDeprecatedGetter(Object obj, boolean isBool, String field)
      throws ReflectiveOperationException {
    try {
      Object val = super.invokeGetter(obj, field);
    } catch (NoSuchFieldException ex) {
      // that's normal: we are testing back compatibility, so there should no field be left

      String prefix = isBool ? "is" : "get";
      String methodName = prefix + field.substring(0, 1).toUpperCase() + field.substring(1);
      // back compatibility getters should be deprecated
      return invokeMethod(obj, methodName, Deprecated.class);
    }
    throw new AssertionError("NoSuchFieldException was expected: there should be no field left");
  }
}
