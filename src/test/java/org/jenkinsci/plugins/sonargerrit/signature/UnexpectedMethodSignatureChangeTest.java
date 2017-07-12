package org.jenkinsci.plugins.sonargerrit.signature;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

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
        Collection<Object> configs = Arrays.asList(config2, config3);

        String className = "org.jenkinsci.plugins.sonargerrit.config.InspectionConfig";
        String[] paramClasses = {};
        Object[] params = {};

        Object c = invokeConstructor(className, paramClasses, params);
        invokeSetter(p, "inspectionConfig", c);
        invokeSetter(p, config1, false, "inspectionConfig", "baseConfig");
        invokeSetter(p, configs, false, "inspectionConfig", "subJobConfigs");

        Assert.assertEquals("http://localhost:9000", invokeGetter(p, "inspectionConfig", "serverURL"));

        Assert.assertEquals(invokeGetter(config1, "projectPath"), invokeGetter(p, "inspectionConfig", "baseConfig", "projectPath"));
        Assert.assertEquals(invokeGetter(config1, "sonarReportPath"), invokeGetter(p, "inspectionConfig", "baseConfig", "sonarReportPath"));

        Assert.assertEquals(invokeGetter(config2, "projectPath"), invokeGetter(p, "inspectionConfig", "subJobConfigs", "projectPath"));
        Assert.assertEquals(invokeGetter(config2, "sonarReportPath"), invokeGetter(p, "inspectionConfig", "subJobConfigs", "sonarReportPath"));
    }

    @Test
    public void testSetNotificationConfig() throws ReflectiveOperationException {
        SonarToGerritPublisher p = invokeConstructor();

        String className = "org.jenkinsci.plugins.sonargerrit.config.NotificationConfig";
        String paramsType = "java.lang.String";
        String[] paramClasses = {paramsType, paramsType, paramsType};
        String[] params = {"ALL", "OWNER_REVIEWERS", "NONE"};

        Object c = invokeConstructor(className, paramClasses, params);
        Assert.assertNotSame(invokeGetter(c, "noIssuesNotificationRecipient"), invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
        Assert.assertNotSame(invokeGetter(c, "commentedIssuesNotificationRecipient"), invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
        Assert.assertNotSame(invokeGetter(c, "negativeScoreNotificationRecipient"), invokeGetter(p, "notificationConfig", "negativeScoreNotificationRecipient"));

        invokeSetter(p, "notificationConfig", c);
        Assert.assertEquals(invokeGetter(c, "noIssuesNotificationRecipient"), invokeGetter(p, "notificationConfig", "noIssuesNotificationRecipient"));
        Assert.assertEquals(invokeGetter(c, "commentedIssuesNotificationRecipient"), invokeGetter(p, "notificationConfig", "commentedIssuesNotificationRecipient"));
        Assert.assertEquals(invokeGetter(c, "negativeScoreNotificationRecipient"), invokeGetter(p, "notificationConfig", "negativeScoreNotificationRecipient"));
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
        Assert.assertNotSame(invokeGetter(c1, "severity"), invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertNotSame(invokeGetter(c1, "newIssuesOnly"), invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertNotSame(invokeGetter(c1, "changedLinesOnly"), invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));

        String className2 = "org.jenkinsci.plugins.sonargerrit.config.ReviewConfig";

        String[] paramClasses2 = {className, stringType, stringType, stringType};
        Object[] params2 = {c1, "Test", "Test", "Test"};

        Object c2 = invokeConstructor(className2, paramClasses2, params2);
        Assert.assertNotSame(invokeGetter(c2, "noIssuesTitleTemplate"), invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
        Assert.assertNotSame(invokeGetter(c2, "someIssuesTitleTemplate"), invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
        Assert.assertNotSame(invokeGetter(c2, "issueCommentTemplate"), invokeGetter(p, "reviewConfig", "issueCommentTemplate"));

        invokeSetter(p, "reviewConfig", c2);
        Assert.assertEquals(invokeGetter(c1, "severity"), invokeGetter(p, "reviewConfig", "issueFilterConfig", "severity"));
        Assert.assertEquals(invokeGetter(c1, "newIssuesOnly"), invokeGetter(p, "reviewConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertEquals(invokeGetter(c1, "changedLinesOnly"), invokeGetter(p, "reviewConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertEquals(invokeGetter(c2, "noIssuesTitleTemplate"), invokeGetter(p, "reviewConfig", "noIssuesTitleTemplate"));
        Assert.assertEquals(invokeGetter(c2, "someIssuesTitleTemplate"), invokeGetter(p, "reviewConfig", "someIssuesTitleTemplate"));
        Assert.assertEquals(invokeGetter(c2, "issueCommentTemplate"), invokeGetter(p, "reviewConfig", "issueCommentTemplate"));
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
//        Assert.assertNotSame(invokeGetter(c1, "severity"), invokeGetter(p, "scoreConfig", "issueFilterConfig", "severity"));
//        Assert.assertNotSame(invokeGetter(c1, "newIssuesOnly"), invokeGetter(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly"));
//        Assert.assertNotSame(invokeGetter(c1, "changedLinesOnly"), invokeGetter(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly"));

        String className2 = "org.jenkinsci.plugins.sonargerrit.config.ScoreConfig";

        String[] paramClasses2 = {className, stringType, integerType, integerType};
        Object[] params2 = {c1, "Test", 2, -2};

        Object c2 = invokeConstructor(className2, paramClasses2, params2);

        Assert.assertNull(invokeGetter(p, "scoreConfig"));
//        Assert.assertNotSame(invokeGetter(c2, "category"), invokeGetter(p, "scoreConfig", "category"));
//        Assert.assertNotSame(invokeGetter(c2, "noIssuesScore"), invokeGetter(p, "scoreConfig", "noIssuesScore"));
//        Assert.assertNotSame(invokeGetter(c2, "issuesScore"), invokeGetter(p, "scoreConfig", "issuesScore"));

        invokeSetter(p, "scoreConfig", c2);
        Assert.assertEquals(invokeGetter(c1, "severity"), invokeGetter(p, "scoreConfig", "issueFilterConfig", "severity"));
        Assert.assertEquals(invokeGetter(c1, "newIssuesOnly"), invokeGetter(p, "scoreConfig", "issueFilterConfig", "newIssuesOnly"));
        Assert.assertEquals(invokeGetter(c1, "changedLinesOnly"), invokeGetter(p, "scoreConfig", "issueFilterConfig", "changedLinesOnly"));
        Assert.assertEquals(invokeGetter(c2, "category"), invokeGetter(p, "scoreConfig", "category"));
        Assert.assertEquals(invokeGetter(c2, "noIssuesScore"), invokeGetter(p, "scoreConfig", "noIssuesScore"));
        Assert.assertEquals(invokeGetter(c2, "issuesScore"), invokeGetter(p, "scoreConfig", "issuesScore"));
    }


}
