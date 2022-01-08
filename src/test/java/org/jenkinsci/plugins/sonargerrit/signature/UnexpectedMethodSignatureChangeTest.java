package org.jenkinsci.plugins.sonargerrit.signature;

import java.util.Arrays;
import java.util.Collection;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 17.11.2017 20:56 $Id$ */

/*
 * If this test fails then probably SonarToGerritPublisher's signature has changed.
 * In order to save plugin configuration that was set up with old configuration, it
 * is necessary to keep old setter methods, mark them as @Deprecated and move test to
 * BackCompatibilityConfigurationTest
 */

public class UnexpectedMethodSignatureChangeTest {

  @Test
  public void testInspectionConfig() throws ReflectiveOperationException {
    SonarToGerritPublisher p = new SonarToGerritPublisher();

    String configClassName = "org.jenkinsci.plugins.sonargerrit.config.SubJobConfig";
    String stringType = "java.lang.String";
    String[] configParamClasses = {stringType, stringType};
    String[] config1Params = {"TEST", "TEST"};
    String[] config2Params = {"TEST2", "TEST2"};
    String[] config3Params = {"TEST3", "TEST3"};

    Object config1 =
        Reflection.invokeConstructor(configClassName, configParamClasses, config1Params);
    Object config2 =
        Reflection.invokeConstructor(configClassName, configParamClasses, config2Params);
    Object config3 =
        Reflection.invokeConstructor(configClassName, configParamClasses, config3Params);
    Collection<Object> configs = Arrays.asList(config2, config3);

    String className = "org.jenkinsci.plugins.sonargerrit.config.InspectionConfig";
    String[] paramClasses = {};
    Object[] params = {};

    Object c = Reflection.invokeConstructor(className, paramClasses, params);
    Reflection.invokeSetter(p, "inspectionConfig", c);
    Reflection.invokeSetter(p, config1, false, "inspectionConfig", "baseConfig");
    Reflection.invokeSetter(p, configs, false, "inspectionConfig", "subJobConfigs");

    Assertions.assertEquals(
        "http://localhost:9000", Reflection.invokeGetter(p, "inspectionConfig", "serverURL"));

    Assertions.assertEquals(
        Reflection.invokeGetter(config1, "projectPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
    Assertions.assertEquals(
        Reflection.invokeGetter(config1, "sonarReportPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

    Assertions.assertEquals(
        Reflection.invokeGetter(config2, "projectPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "subJobConfigs", "projectPath"));
    Assertions.assertEquals(
        Reflection.invokeGetter(config2, "sonarReportPath"),
        Reflection.invokeGetter(p, "inspectionConfig", "subJobConfigs", "sonarReportPath"));
  }

  @Test
  public void testSetNotificationConfig() throws ReflectiveOperationException {
    SonarToGerritPublisher p = new SonarToGerritPublisher();

    String className = "org.jenkinsci.plugins.sonargerrit.config.NotificationConfig";
    String paramsType = "java.lang.String";
    String[] paramClasses = {paramsType, paramsType, paramsType};
    String[] params = {"ALL", "OWNER_REVIEWERS", "NONE"};

    Object c = Reflection.invokeConstructor(className, paramClasses, params);
    Assertions.assertNotSame(
        Reflection.invokeGetter(c, "noIssuesNotificationRecipient"),
        Reflection.invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
    Assertions.assertNotSame(
        Reflection.invokeGetter(c, "commentedIssuesNotificationRecipient"),
        Reflection.invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
    Assertions.assertNotSame(
        Reflection.invokeGetter(c, "negativeScoreNotificationRecipient"),
        Reflection.invokeGetter(p, "notificationConfig", "negativeScoreNotificationRecipient"));

    Reflection.invokeSetter(p, "notificationConfig", c);
    Assertions.assertEquals(
        Reflection.invokeGetter(c, "noIssuesNotificationRecipient"),
        Reflection.invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c, "commentedIssuesNotificationRecipient"),
        Reflection.invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c, "negativeScoreNotificationRecipient"),
        Reflection.invokeGetter(p, "notificationConfig", "negativeScoreNotificationRecipient"));
  }

  @Test
  public void testSetReviewConfig() throws ReflectiveOperationException {
    SonarToGerritPublisher p = new SonarToGerritPublisher();

    String className = "org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig";
    String stringType = "java.lang.String";
    String booleanType = "boolean";

    String[] paramClasses = {stringType, booleanType, booleanType};
    Object[] params = {"BLOCKER", true, true};

    Object c1 = Reflection.invokeConstructor(className, paramClasses, params);
    Assertions.assertNotSame(
        Reflection.invokeGetter(c1, "severity"),
        Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertNotSame(
        Reflection.invokeGetter(c1, "newIssuesOnly"),
        Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertNotSame(
        Reflection.invokeGetter(c1, "changedLinesOnly"),
        Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));

    String className2 = "org.jenkinsci.plugins.sonargerrit.config.ReviewConfig";

    String[] paramClasses2 = {className, stringType, stringType, stringType};
    Object[] params2 = {c1, "Test", "Test", "Test"};

    Object c2 = Reflection.invokeConstructor(className2, paramClasses2, params2);
    Assertions.assertNotSame(
        Reflection.invokeGetter(c2, "noIssuesTitleTemplate"),
        Reflection.invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
    Assertions.assertNotSame(
        Reflection.invokeGetter(c2, "someIssuesTitleTemplate"),
        Reflection.invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
    Assertions.assertNotSame(
        Reflection.invokeGetter(c2, "issueCommentTemplate"),
        Reflection.invokeGetter(p, "reviewConfig", "issueCommentTemplate"));

    Reflection.invokeSetter(p, "reviewConfig", c2);
    Assertions.assertEquals(
        Reflection.invokeGetter(c1, "severity"),
        Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c1, "newIssuesOnly"),
        Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c1, "changedLinesOnly"),
        Reflection.invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c2, "noIssuesTitleTemplate"),
        Reflection.invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c2, "someIssuesTitleTemplate"),
        Reflection.invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c2, "issueCommentTemplate"),
        Reflection.invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
  }

  @Test
  public void testSetScoreConfig() throws ReflectiveOperationException {
    SonarToGerritPublisher p = new SonarToGerritPublisher();

    String className = "org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig";
    String stringType = "java.lang.String";
    String booleanType = "boolean";
    String integerType = "java.lang.Integer";

    String[] paramClasses = {stringType, booleanType, booleanType};
    Object[] params = {"BLOCKER", true, true};

    Object c1 = Reflection.invokeConstructor(className, paramClasses, params);

    String className2 = "org.jenkinsci.plugins.sonargerrit.config.ScoreConfig";

    String[] paramClasses2 = {className, stringType, integerType, integerType};
    Object[] params2 = {c1, "Test", 2, -2};

    Object c2 = Reflection.invokeConstructor(className2, paramClasses2, params2);

    Assertions.assertNull(Reflection.invokeGetter(p, "scoreConfig"));

    Reflection.invokeSetter(p, "scoreConfig", c2);
    Assertions.assertEquals(
        Reflection.invokeGetter(c1, "severity"),
        Reflection.invokeGetter(p, "scoreConfig", "issueFilterConfig", "severity"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c1, "newIssuesOnly"),
        Reflection.invokeGetter(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c1, "changedLinesOnly"),
        Reflection.invokeGetter(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c2, "category"),
        Reflection.invokeGetter(p, "scoreConfig", "category"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c2, "noIssuesScore"),
        Reflection.invokeGetter(p, "scoreConfig", "noIssuesScore"));
    Assertions.assertEquals(
        Reflection.invokeGetter(c2, "issuesScore"),
        Reflection.invokeGetter(p, "scoreConfig", "issuesScore"));
  }
}
