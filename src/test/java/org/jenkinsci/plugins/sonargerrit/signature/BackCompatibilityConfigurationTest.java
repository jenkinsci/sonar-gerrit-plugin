package org.jenkinsci.plugins.sonargerrit.signature;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.stapler.DataBoundSetter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 17.11.2017 20:54 $Id$ */

/*
 * This class checks if it is still possible to keep configuration settings from previous plugin version
 * Methods left for back compatibility purposes should be @Deprecated
 */
@EnableJenkinsRule
public class BackCompatibilityConfigurationTest {

  @Test
  public void testSetSeverity() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String severity = "BLOCKER";
    Assertions.assertNotSame(
        severity, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig")); //
    Reflection.invokeSetter(p, "severity", severity);

    Assertions.assertEquals(
        severity, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));

    severity = "CRITICAL";
    Assertions.assertNotSame(
        severity, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Reflection.invokeSetter(p, "postScore", true);
    Reflection.invokeSetter(p, "severity", severity);
    Assertions.assertEquals(
        severity, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertEquals(
        severity, Reflection.invokeGetter(p, "scoreConfig", "issueFilterConfig", "severity"));
  }

  @Test
  public void testSetChangedLinesOnly() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    Assertions.assertNotSame(
        true, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig")); //
    Reflection.invokeSetter(p, "changedLinesOnly", true);
    Assertions.assertEquals(
        true, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));

    Assertions.assertNotSame(
        false, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Reflection.invokeSetter(p, "postScore", true);
    Reflection.invokeSetter(p, "changedLinesOnly", false);
    Assertions.assertEquals(
        false, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertEquals(
        false,
        Reflection.invokeGetter(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly")); //
  }

  @Test
  public void testSetNewIssuesOnly() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    boolean newIssuesOnly = true;
    Assertions.assertNotSame(
        true, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig")); //
    Reflection.invokeSetter(p, "newIssuesOnly", true);
    Assertions.assertEquals(
        true, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));

    newIssuesOnly = false;
    Assertions.assertNotSame(
        false, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Reflection.invokeSetter(p, "postScore", true);
    Reflection.invokeSetter(p, "newIssuesOnly", false);
    Assertions.assertEquals(
        false, Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertEquals(
        false, Reflection.invokeGetter(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly")); //
  }

  @Test
  public void testSetNoIssuesToPostText() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String noIssuesToPostText = "Test";
    Assertions.assertNotSame(
        noIssuesToPostText, Reflection.invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
    Reflection.invokeSetter(p, "noIssuesToPostText", noIssuesToPostText);
    Assertions.assertEquals(
        noIssuesToPostText, Reflection.invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
  }

  @Test
  public void testSetSomeIssuesToPostText() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String someIssuesToPostText = "Test";
    Assertions.assertNotSame(
        someIssuesToPostText,
        Reflection.invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
    Reflection.invokeSetter(p, "someIssuesToPostText", someIssuesToPostText);
    Assertions.assertEquals(
        someIssuesToPostText,
        Reflection.invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
  }

  @Test
  public void testSetIssueComment() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String issueComment = "Test";
    Assertions.assertNotSame(
        issueComment, Reflection.invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
    Reflection.invokeSetter(p, "issueComment", issueComment);
    Assertions.assertEquals(
        issueComment, Reflection.invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
  }

  @Test
  public void testSetOverrideCredentials() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    boolean overrideCredentials = true;
    Assertions.assertNull(Reflection.invokeGetter(p, "authConfig"));
    Reflection.invokeSetter(p, "overrideCredentials", overrideCredentials);
    Assertions.assertNotNull(Reflection.invokeGetter(p, "authConfig"));

    // todo check false
  }

  @Test
  public void testSetHttpUsername() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String httpUsername = "Test";
    Assertions.assertNull(Reflection.invokeGetter(p, "authConfig"));
    Reflection.invokeSetter(p, "httpUsername", httpUsername);
    Assertions.assertNull(Reflection.invokeGetter(p, "authConfig"));

    Reflection.invokeSetter(p, "overrideCredentials", true);
    //        invokeSetter(p, "httpUsername", httpUsername);
    Assertions.assertNotNull(Reflection.invokeGetter(p, "authConfig"));
    Assertions.assertEquals(httpUsername, Reflection.invokeGetter(p, "authConfig", "username"));
  }

  @Test
  public void testSetPostScore() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    boolean postScore = true;
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));
    Reflection.invokeSetter(p, "postScore", postScore);
    Assertions.assertNotNull(Reflection.invokeGetter(p, "scoreConfig"));
  }

  @Test
  public void testSetCategory() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String category = "Test";
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));
    Reflection.invokeSetter(p, "category", category);
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));

    Reflection.invokeSetter(p, "postScore", true);
    //        invokeSetter(p, "category", category);
    Assertions.assertNotNull(Reflection.invokeGetter(p, "scoreConfig"));
    Assertions.assertEquals(category, Reflection.invokeGetter(p, "scoreConfig", "category"));
  }

  @Test
  public void testSetNoIssuesScore() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String noIssuesScore = "2";
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));
    Reflection.invokeSetter(p, "noIssuesScore", noIssuesScore);
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));

    Reflection.invokeSetter(p, "postScore", true);
    //        invokeSetter(p, "noIssuesScore", noIssuesScore);
    Assertions.assertNotNull(Reflection.invokeGetter(p, "scoreConfig"));
    Assertions.assertEquals(
        noIssuesScore, Reflection.invokeGetter(p, "scoreConfig", "noIssuesScore").toString());
  }

  @Test
  public void testSetIssuesScore() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String issuesScore = "-2";
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));
    Reflection.invokeSetter(p, "issuesScore", issuesScore);
    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));

    Reflection.invokeSetter(p, "postScore", true);
    //        invokeSetter(p, "issuesScore", issuesScore);
    Assertions.assertNotNull(Reflection.invokeGetter(p, "scoreConfig"));
    Assertions.assertEquals(
        issuesScore, Reflection.invokeGetter(p, "scoreConfig", "issuesScore").toString());
  }

  @Test
  public void testSetNoIssuesNotification() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String noIssuesNotification = "ALL";
    Assertions.assertNotSame(
        noIssuesNotification,
        Reflection.invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
    Reflection.invokeSetter(p, "noIssuesNotification", noIssuesNotification);
    Assertions.assertEquals(
        noIssuesNotification,
        Reflection.invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
  }

  @Test
  public void testSetIssuesNotification() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String issuesNotification = "ALL";
    Assertions.assertNotSame(
        issuesNotification,
        Reflection.invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
    Reflection.invokeSetter(p, "issuesNotification", issuesNotification);
    Assertions.assertEquals(
        issuesNotification,
        Reflection.invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
  }

  @Test
  public void testSetProjectPath() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String path = "Test";
    Assertions.assertNotSame(
        path, Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Reflection.invokeSetter(p, "projectPath", path);
    Assertions.assertEquals(
        path, Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
  }

  @Test
  public void testSetPath() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String path = "Test";
    Assertions.assertNotSame(
        path, Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
    Reflection.invokeSetter(p, "path", path);
    Assertions.assertEquals(
        path, Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
  }

  @Test
  public void testSetSonarURL() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();
    String sonarURL = UUID.randomUUID().toString();
    Assertions.assertNotSame(sonarURL, Reflection.invokeGetter(p, "inspectionConfig", "serverURL"));
    Reflection.invokeSetter(p, "sonarURL", sonarURL);
    Assertions.assertEquals(sonarURL, Reflection.invokeGetter(p, "inspectionConfig", "serverURL"));
  }

  @Test
  public void testSubJobConfig() throws ReflectiveOperationException {
    SonarToGerritPublisher p = Reflection.invokeConstructor();

    String className = "org.jenkinsci.plugins.sonargerrit.config.SubJobConfig";
    String paramsType = "java.lang.String";
    String[] paramClasses = {paramsType, paramsType};
    String[] params = {"TEST", "TEST"};

    Object c = Reflection.invokeConstructor(className, paramClasses, params);
    Assertions.assertNotSame(
        Reflection.invokeGetter(c, "projectPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Assertions.assertNotSame(
        Reflection.invokeGetter(c, "sonarReportPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

    List<Object> value = new LinkedList<>();
    value.add(c);
    //        next string doesn't work because value type is LinkedList, but setter parameter type
    // is List.
    //        invokeSetter(p, "subJobConfigs", value);
    Reflection.invokeMethod(p, "setSubJobConfigs", value, Deprecated.class, DataBoundSetter.class);
    Assertions.assertEquals(
        Reflection.invokeGetter(c, "projectPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c, "sonarReportPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));
  }

  protected void invokeSetter(SonarToGerritPublisher obj, String field, Object value)
      throws ReflectiveOperationException {
    Reflection.invokeSetter(obj, field, value, true);
  }

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
      return Reflection.invokeGetter(obj, field);
    }
  }

  private Object invokeDeprecatedGetter(Object obj, boolean isBool, String field)
      throws ReflectiveOperationException {
    try {
      Reflection.invokeGetter(obj, field);
    } catch (NoSuchFieldException ex) {
      // that's normal: we are testing back compatibility, so there should no field be left

      String prefix = isBool ? "is" : "get";
      String methodName = prefix + field.substring(0, 1).toUpperCase() + field.substring(1);
      // back compatibility getters should be deprecated
      return Reflection.invokeMethod(obj, methodName, Deprecated.class);
    }
    throw new AssertionError("NoSuchFieldException was expected: there should be no field left");
  }
}
