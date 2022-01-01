package org.jenkinsci.plugins.sonargerrit.signature;

import java.util.LinkedList;
import java.util.List;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
    Assertions.assertNotSame(
        severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertNull(invokeGetter(p, "scoreConfig")); //
    invokeSetter(p, "severity", severity);

    Assertions.assertEquals(
        severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));

    severity = "CRITICAL";
    Assertions.assertNotSame(
        severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    invokeSetter(p, "postScore", true);
    invokeSetter(p, "severity", severity);
    Assertions.assertEquals(
        severity, invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertEquals(
        severity, invokeGetter(p, "scoreConfig", "issueFilterConfig", "severity"));
  }

  @Test
  public void testSetChangedLinesOnly() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    Assertions.assertNotSame(
        true, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertNull(invokeGetter(p, "scoreConfig")); //
    invokeSetter(p, "changedLinesOnly", true);
    Assertions.assertEquals(
        true, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));

    Assertions.assertNotSame(
        false, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    invokeSetter(p, "postScore", true);
    invokeSetter(p, "changedLinesOnly", false);
    Assertions.assertEquals(
        false, invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertEquals(
        false, invokeGetter(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly")); //
  }

  @Test
  public void testSetNewIssuesOnly() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    boolean newIssuesOnly = true;
    Assertions.assertNotSame(
        true, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertNull(invokeGetter(p, "scoreConfig")); //
    invokeSetter(p, "newIssuesOnly", true);
    Assertions.assertEquals(
        true, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));

    newIssuesOnly = false;
    Assertions.assertNotSame(
        false, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    invokeSetter(p, "postScore", true);
    invokeSetter(p, "newIssuesOnly", false);
    Assertions.assertEquals(
        false, invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertEquals(
        false, invokeGetter(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly")); //
  }

  @Test
  public void testSetNoIssuesToPostText() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String noIssuesToPostText = "Test";
    Assertions.assertNotSame(
        noIssuesToPostText, invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
    invokeSetter(p, "noIssuesToPostText", noIssuesToPostText);
    Assertions.assertEquals(
        noIssuesToPostText, invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
  }

  @Test
  public void testSetSomeIssuesToPostText() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String someIssuesToPostText = "Test";
    Assertions.assertNotSame(
        someIssuesToPostText, invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
    invokeSetter(p, "someIssuesToPostText", someIssuesToPostText);
    Assertions.assertEquals(
        someIssuesToPostText, invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
  }

  @Test
  public void testSetIssueComment() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String issueComment = "Test";
    Assertions.assertNotSame(issueComment, invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
    invokeSetter(p, "issueComment", issueComment);
    Assertions.assertEquals(issueComment, invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
  }

  @Test
  public void testSetOverrideCredentials() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    boolean overrideCredentials = true;
    Assertions.assertNull(invokeGetter(p, "authConfig"));
    invokeSetter(p, "overrideCredentials", overrideCredentials);
    Assertions.assertNotNull(invokeGetter(p, "authConfig"));

    // todo check false
  }

  @Test
  public void testSetHttpUsername() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String httpUsername = "Test";
    Assertions.assertNull(invokeGetter(p, "authConfig"));
    invokeSetter(p, "httpUsername", httpUsername);
    Assertions.assertNull(invokeGetter(p, "authConfig"));

    invokeSetter(p, "overrideCredentials", true);
    //        invokeSetter(p, "httpUsername", httpUsername);
    Assertions.assertNotNull(invokeGetter(p, "authConfig"));
    Assertions.assertEquals(httpUsername, invokeGetter(p, "authConfig", "username"));
  }

  @Test
  public void testSetPostScore() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    boolean postScore = true;
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));
    invokeSetter(p, "postScore", postScore);
    Assertions.assertNotNull(invokeGetter(p, "scoreConfig"));
  }

  @Test
  public void testSetCategory() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String category = "Test";
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));
    invokeSetter(p, "category", category);
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));

    invokeSetter(p, "postScore", true);
    //        invokeSetter(p, "category", category);
    Assertions.assertNotNull(invokeGetter(p, "scoreConfig"));
    Assertions.assertEquals(category, invokeGetter(p, "scoreConfig", "category"));
  }

  @Test
  public void testSetNoIssuesScore() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String noIssuesScore = "2";
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));
    invokeSetter(p, "noIssuesScore", noIssuesScore);
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));

    invokeSetter(p, "postScore", true);
    //        invokeSetter(p, "noIssuesScore", noIssuesScore);
    Assertions.assertNotNull(invokeGetter(p, "scoreConfig"));
    Assertions.assertEquals(
        noIssuesScore, invokeGetter(p, "scoreConfig", "noIssuesScore").toString());
  }

  @Test
  public void testSetIssuesScore() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String issuesScore = "-2";
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));
    invokeSetter(p, "issuesScore", issuesScore);
    Assertions.assertNull(invokeGetter(p, "scoreConfig"));

    invokeSetter(p, "postScore", true);
    //        invokeSetter(p, "issuesScore", issuesScore);
    Assertions.assertNotNull(invokeGetter(p, "scoreConfig"));
    Assertions.assertEquals(issuesScore, invokeGetter(p, "scoreConfig", "issuesScore").toString());
  }

  @Test
  public void testGetter() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    Assertions.assertNull(invokeGetter(p, true, "severity"));
    Assertions.assertFalse((boolean) invokeGetter(p, true, true, "newIssuesOnly"));
    Assertions.assertFalse((boolean) invokeGetter(p, true, true, "changedLinesOnly"));
    Assertions.assertNull(invokeGetter(p, true, "noIssuesToPostText"));
    Assertions.assertNull(invokeGetter(p, true, "someIssuesToPostText"));
    Assertions.assertNull(invokeGetter(p, true, "issueComment"));
    Assertions.assertFalse((boolean) invokeGetter(p, true, true, "overrideCredentials"));
    Assertions.assertNull(invokeGetter(p, true, "httpUsername"));
    Assertions.assertNull(invokeGetter(p, true, "httpPassword"));
    Assertions.assertFalse((boolean) invokeGetter(p, true, true, "postScore"));
    Assertions.assertNull(invokeGetter(p, true, "category"));
    Assertions.assertNull(invokeGetter(p, true, "noIssuesScore"));
    Assertions.assertNull(invokeGetter(p, true, "issuesScore"));
    Assertions.assertNull(invokeGetter(p, true, "subJobConfigs"));
    Assertions.assertNull(invokeGetter(p, true, "projectPath"));
    Assertions.assertNull(invokeGetter(p, true, "path"));
    Assertions.assertNull(invokeGetter(p, true, "noIssuesNotification"));
    Assertions.assertNull(invokeGetter(p, true, "issuesNotification"));
  }

  @Test
  public void testSetNoIssuesNotification() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String noIssuesNotification = "ALL";
    Assertions.assertNotSame(
        noIssuesNotification,
        invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
    invokeSetter(p, "noIssuesNotification", noIssuesNotification);
    Assertions.assertEquals(
        noIssuesNotification,
        invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
  }

  @Test
  public void testSetIssuesNotification() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String issuesNotification = "ALL";
    Assertions.assertNotSame(
        issuesNotification,
        invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
    invokeSetter(p, "issuesNotification", issuesNotification);
    Assertions.assertEquals(
        issuesNotification,
        invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
  }

  @Test
  public void testSetProjectPath() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String path = "Test";
    Assertions.assertNotSame(
        path, invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    invokeSetter(p, "projectPath", path);
    Assertions.assertEquals(path, invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
  }

  @Test
  public void testSetPath() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String path = "Test";
    Assertions.assertNotSame(
        path, invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
    invokeSetter(p, "path", path);
    Assertions.assertEquals(
        path, invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
  }

  @Test
  public void testSetSonarURL() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();
    String sonarURL = "test";
    Assertions.assertNotSame(sonarURL, invokeGetter(p, "inspectionConfig", "serverURL"));
    invokeSetter(p, "sonarURL", "test");
    Assertions.assertEquals("test", invokeGetter(p, "inspectionConfig", "serverURL"));
  }

  @Test
  public void testSubJobConfig() throws ReflectiveOperationException {
    SonarToGerritPublisher p = invokeConstructor();

    String className = "org.jenkinsci.plugins.sonargerrit.config.SubJobConfig";
    String paramsType = "java.lang.String";
    String[] paramClasses = {paramsType, paramsType};
    String[] params = {"TEST", "TEST"};

    Object c = invokeConstructor(className, paramClasses, params);
    Assertions.assertNotSame(
        invokeGetter(c, "projectPath"),
        invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Assertions.assertNotSame(
        invokeGetter(c, "sonarReportPath"),
        invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

    List<Object> value = new LinkedList<>();
    value.add(c);
    //        next string doesn't work because value type is LinkedList, but setter parameter type
    // is List.
    //        invokeSetter(p, "subJobConfigs", value);
    invokeMethod(p, "setSubJobConfigs", value, Deprecated.class, DataBoundSetter.class);
    Assertions.assertEquals(
        invokeGetter(c, "projectPath"),
        invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Assertions.assertEquals(
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
      super.invokeGetter(obj, field);
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
