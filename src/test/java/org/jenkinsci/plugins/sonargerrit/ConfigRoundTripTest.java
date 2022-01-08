package org.jenkinsci.plugins.sonargerrit;

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.config.GerritAuthenticationConfig;
import org.jenkinsci.plugins.sonargerrit.config.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class ConfigRoundTripTest {

  @Test
  void test(JenkinsRule jenkinsRule) throws Exception {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();

    SonarToGerritPublisher before = new SonarToGerritPublisher();
    project.getPublishersList().add(before);

    before.getInspectionConfig().setSonarQubeInstallationName(UUID.randomUUID().toString());
    before.getInspectionConfig().setBaseConfig(new SubJobConfig());
    before.getInspectionConfig().getBaseConfig().setAutoMatch(true);
    before.getInspectionConfig().getBaseConfig().setProjectPath("foo");
    before.getInspectionConfig().getBaseConfig().setSonarReportPath("bar");

    before.getReviewConfig().getIssueFilterConfig().setChangedLinesOnly(true);
    before.getReviewConfig().getIssueFilterConfig().setNewIssuesOnly(true);
    before.getReviewConfig().getIssueFilterConfig().setSeverity("BLOCKER");

    before.setScoreConfig(new ScoreConfig());
    before.getScoreConfig().getIssueFilterConfig().setChangedLinesOnly(true);
    before.getScoreConfig().getIssueFilterConfig().setNewIssuesOnly(true);
    before.getScoreConfig().getIssueFilterConfig().setSeverity("BLOCKER");
    before.getScoreConfig().setNoIssuesScore(20);
    before.getScoreConfig().setIssuesScore(-20);
    before.getScoreConfig().setCategory("Foo-Label");

    before.getNotificationConfig().setNoIssuesNotificationRecipient("foo");
    before.getNotificationConfig().setCommentedIssuesNotificationRecipient("bar");
    before.getNotificationConfig().setNegativeScoreNotificationRecipient("baz");

    before.setAuthConfig(new GerritAuthenticationConfig());
    before.getAuthConfig().setUsername("john.doe");
    before.getAuthConfig().setSecretPassword(Secret.fromString("secret"));

    jenkinsRule.submit(
        jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

    SonarToGerritPublisher after = project.getPublishersList().get(SonarToGerritPublisher.class);
    jenkinsRule.assertEqualBeans(
        before,
        after,
        String.join(
            ",",
            "inspectionConfig.sonarQubeInstallationName",
            "inspectionConfig.baseConfig.autoMatch",
            "inspectionConfig.baseConfig.projectPath",
            "inspectionConfig.baseConfig.sonarReportPath",
            "reviewConfig.issueFilterConfig.changedLinesOnly",
            "reviewConfig.issueFilterConfig.newIssuesOnly",
            "reviewConfig.issueFilterConfig.severity",
            "scoreConfig.issueFilterConfig.changedLinesOnly",
            "scoreConfig.issueFilterConfig.newIssuesOnly",
            "scoreConfig.issueFilterConfig.severity",
            "scoreConfig.noIssuesScore",
            "scoreConfig.issuesScore",
            "scoreConfig.category",
            "notificationConfig.noIssuesNotificationRecipient",
            "notificationConfig.commentedIssuesNotificationRecipient",
            "notificationConfig.negativeScoreNotificationRecipient",
            "authConfig.username",
            "authConfig.secretPassword"));
  }
}
