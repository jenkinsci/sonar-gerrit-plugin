package org.jenkinsci.plugins.sonargerrit.signature;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 17.11.2017 20:56
 * $Id$
 */

/*
 * If this test fails then probably SonarToGerritPublisher's signature has changed.
 * In order to save plugin configuration that was set up with old configuration, it
 * is necessary to keep old setter methods, mark them as @Deprecated and move test to
 * BackCompatibilityConfigurationTest
 */

public class UnexpectedMethodSignatureChangeTest extends ConfigurationUpdateTest {

    @Test
    public void testInspectionConfig() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();

        String configClassName = "org.jenkinsci.plugins.sonargerrit.config.SubJobConfig";
        String stringType = "java.lang.String";
        String[] configParamClasses = {stringType, stringType};
        String[] config1Params = {"TEST", "TEST"};
        String[] config2Params = {"TEST2", "TEST2"};
        String[] config3Params = {"TEST3", "TEST3"};


        Object config1 = invokeConstructor(configClassName, configParamClasses, config1Params);
        Object config2 = invokeConstructor(configClassName, configParamClasses, config2Params);
        Object config3 = invokeConstructor(configClassName, configParamClasses, config3Params);
        List configs = Arrays.asList(config2, config3);

        String className = "org.jenkinsci.plugins.sonargerrit.config.InspectionConfig";
        String subJobType = "org.jenkinsci.plugins.sonargerrit.config.SubJobConfig";
        String collectionType = "java.util.List";
        String[] paramClasses = {stringType, subJobType, collectionType};
        Object[] params = {"http://localhost:9000", config1, configs};

        Object c = invokeConstructor(className, paramClasses, params);
        invokeSetter(p, "inspectionConfig", c);

        Assert.assertEquals("http://localhost:9000", readFieldValue(p, "inspectionConfig", "serverURL"));

        Assert.assertEquals(readFieldValue(config1, "projectPath"), readFieldValue(p, "inspectionConfig", "baseConfig",  "projectPath"));
        Assert.assertEquals(readFieldValue(config1, "sonarReportPath"), readFieldValue(p, "inspectionConfig", "baseConfig",  "sonarReportPath"));

        Assert.assertEquals(readFieldValue(config2, "projectPath"), readFieldValue(p, "inspectionConfig", "subJobConfigs",  "projectPath"));
        Assert.assertEquals(readFieldValue(config2, "sonarReportPath"), readFieldValue(p, "inspectionConfig", "subJobConfigs",  "sonarReportPath"));
    }

    @Test
    public void testSetSonarURL() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();
        String sonarURL = "test";
        Assert.assertNotSame(sonarURL, readFieldValue(p, "inspectionConfig", "serverURL"));
        invokeSetter(p, "sonarURL", "test");
        Assert.assertEquals("test", readFieldValue(p, "inspectionConfig", "serverURL"));
    }

    @Test
    public void testSubJobConfig() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();

        String className = "org.jenkinsci.plugins.sonargerrit.config.SubJobConfig";
        String paramsType = "java.lang.String";
        String[] paramClasses = {paramsType, paramsType};
        String[] params = {"TEST", "TEST"};

        Object c = invokeConstructor(className, paramClasses, params);
        Assert.assertNotSame(readFieldValue(c, "projectPath"), readFieldValue(p, "inspectionConfig", "baseConfig", "projectPath"));
        Assert.assertNotSame(readFieldValue(c, "sonarReportPath"), readFieldValue(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

        List value = new LinkedList<>();
        value.add(c);
        invokeSetter(p, "subJobConfigs", value);
        Assert.assertEquals(readFieldValue(c, "projectPath"), readFieldValue(p, "inspectionConfig", "baseConfig",  "projectPath"));
        Assert.assertEquals(readFieldValue(c, "sonarReportPath"), readFieldValue(p, "inspectionConfig", "baseConfig",  "sonarReportPath"));

    }

    @Test
    public void testSetNotificationConfig() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();

        String className = "org.jenkinsci.plugins.sonargerrit.config.NotificationConfig";
        String paramsType = "java.lang.String";
        String[] paramClasses = {paramsType, paramsType, paramsType};
        String[] params = {"ALL", "OWNER_REVIEWERS", "NONE"};

        Object c = invokeConstructor(className, paramClasses, params);
        Assert.assertNotSame(readFieldValue(c, "noIssuesNotificationRecipient"), readFieldValue(p, "notificationConfig", "noIssuesNotificationRecipient"));
        Assert.assertNotSame(readFieldValue(c, "commentedIssuesNotificationRecipient"), readFieldValue(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
        Assert.assertNotSame(readFieldValue(c, "negativeScoreNotificationRecipient"), readFieldValue(p, "notificationConfig", "negativeScoreNotificationRecipient"));

        invokeSetter(p, "notificationConfig", c);
        Assert.assertEquals(readFieldValue(c, "noIssuesNotificationRecipient"), readFieldValue(p, "notificationConfig", "noIssuesNotificationRecipient"));
        Assert.assertEquals(readFieldValue(c, "commentedIssuesNotificationRecipient"), readFieldValue(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
        Assert.assertEquals(readFieldValue(c, "negativeScoreNotificationRecipient"), readFieldValue(p, "notificationConfig", "negativeScoreNotificationRecipient"));
    }

    @Test
    public void testSetReviewConfig() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();

        String className = "org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig";
        String stringType = "java.lang.String";
        String booleanType = "boolean";

        String[] paramClasses = {stringType, booleanType, booleanType};
        Object[] params = {"BLOCKER", true, true};

        Object c1 = invokeConstructor(className, paramClasses, params);
        Assert.assertNotSame(readFieldValue(c1, "severity"), readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertNotSame(readFieldValue(c1, "newIssuesOnly"), readFieldValue(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertNotSame(readFieldValue(c1, "changedLinesOnly"), readFieldValue(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));

        String className2 = "org.jenkinsci.plugins.sonargerrit.config.ReviewConfig";

        String[] paramClasses2 = {className, stringType, stringType, stringType};
        Object[] params2 = {c1, "Test", "Test", "Test"};

        Object c2 = invokeConstructor(className2, paramClasses2, params2);
        Assert.assertNotSame(readFieldValue(c2, "noIssuesTitleTemplate"), readFieldValue(p, "reviewConfig", "noIssuesTitleTemplate"));
        Assert.assertNotSame(readFieldValue(c2, "someIssuesTitleTemplate"), readFieldValue(p, "reviewConfig", "someIssuesTitleTemplate"));
        Assert.assertNotSame(readFieldValue(c2, "issueCommentTemplate"), readFieldValue(p, "reviewConfig", "issueCommentTemplate"));

        invokeSetter(p, "reviewConfig", c2);
        Assert.assertEquals(readFieldValue(c1, "severity"), readFieldValue(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertEquals(readFieldValue(c1, "newIssuesOnly"), readFieldValue(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertEquals(readFieldValue(c1, "changedLinesOnly"), readFieldValue(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertEquals(readFieldValue(c2, "noIssuesTitleTemplate"), readFieldValue(p, "reviewConfig", "noIssuesTitleTemplate"));
        Assert.assertEquals(readFieldValue(c2, "someIssuesTitleTemplate"), readFieldValue(p, "reviewConfig", "someIssuesTitleTemplate"));
        Assert.assertEquals(readFieldValue(c2, "issueCommentTemplate"), readFieldValue(p, "reviewConfig", "issueCommentTemplate"));
    }

    @Test
    public void testSetScoreConfig() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();

        String className = "org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig";
        String stringType = "java.lang.String";
        String booleanType = "boolean";
        String integerType = "java.lang.Integer";

        String[] paramClasses = {stringType, booleanType, booleanType};
        Object[] params = {"BLOCKER", true, true};

        Object c1 = invokeConstructor(className, paramClasses, params);
//        Assert.assertNotSame(readFieldValue(c1, "severity"), readFieldValue(p, "scoreConfig", "issueFilterConfig", "severity"));
//        Assert.assertNotSame(readFieldValue(c1, "newIssuesOnly"), readFieldValue(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly"));
//        Assert.assertNotSame(readFieldValue(c1, "changedLinesOnly"), readFieldValue(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly"));

        String className2 = "org.jenkinsci.plugins.sonargerrit.config.ScoreConfig";

        String[] paramClasses2 = {className, stringType, integerType, integerType};
        Object[] params2 = {c1, "Test", 2, -2};

        Object c2 = invokeConstructor(className2, paramClasses2, params2);

        Assert.assertNull(readFieldValue(p, "scoreConfig"));
//        Assert.assertNotSame(readFieldValue(c2, "category"), readFieldValue(p, "scoreConfig", "category"));
//        Assert.assertNotSame(readFieldValue(c2, "noIssuesScore"), readFieldValue(p, "scoreConfig", "noIssuesScore"));
//        Assert.assertNotSame(readFieldValue(c2, "issuesScore"), readFieldValue(p, "scoreConfig", "issuesScore"));

        invokeSetter(p, "scoreConfig", c2);
        Assert.assertEquals(readFieldValue(c1, "severity"), readFieldValue(p, "scoreConfig", "issueFilterConfig", "severity"));
        Assert.assertEquals(readFieldValue(c1, "newIssuesOnly"), readFieldValue(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertEquals(readFieldValue(c1, "changedLinesOnly"), readFieldValue(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertEquals(readFieldValue(c2, "category"), readFieldValue(p, "scoreConfig", "category"));
        Assert.assertEquals(readFieldValue(c2, "noIssuesScore"), readFieldValue(p, "scoreConfig", "noIssuesScore"));
        Assert.assertEquals(readFieldValue(c2, "issuesScore"), readFieldValue(p, "scoreConfig", "issuesScore"));
    }


}
