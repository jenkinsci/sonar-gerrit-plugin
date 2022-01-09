package org.jenkinsci.plugins.sonargerrit;

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritAuthenticationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.PreviewModeAnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class ConfigRoundTripTest {

  @Test
  @DisplayName("351.vb_8d85df69260")
  void test1(JenkinsRule jenkinsRule) throws Exception {
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

    before.setAuthConfig(
        new GerritAuthenticationConfig(null, "john.doe", Secret.fromString("secret"), null));

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

  @Test
  @DisplayName("353.v20e9cff705d1")
  void test2(JenkinsRule jenkinsRule) throws Exception {
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

    before.setAuthConfig(new GerritAuthenticationConfig(UUID.randomUUID().toString()));

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
            "authConfig.httpCredentialsId"));
  }

  @Test
  @DisplayName("next")
  void test3(JenkinsRule jenkinsRule) throws Exception {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();

    SonarToGerritPublisher before = new SonarToGerritPublisher();
    project.getPublishersList().add(before);

    before.getInspectionConfig().setSonarQubeInstallationName(UUID.randomUUID().toString());
    PreviewModeAnalysisStrategy previewModeAnalysisStrategy = new PreviewModeAnalysisStrategy();
    before.getInspectionConfig().setAnalysisStrategy(previewModeAnalysisStrategy);
    previewModeAnalysisStrategy.setBaseConfig(new SubJobConfig());
    previewModeAnalysisStrategy.getBaseConfig().setAutoMatch(true);
    previewModeAnalysisStrategy.getBaseConfig().setProjectPath("foo");
    previewModeAnalysisStrategy.getBaseConfig().setSonarReportPath("bar");

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

    before.setAuthConfig(new GerritAuthenticationConfig(UUID.randomUUID().toString()));

    jenkinsRule.submit(
        jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

    SonarToGerritPublisher after = project.getPublishersList().get(SonarToGerritPublisher.class);
    jenkinsRule.assertEqualBeans(
        before,
        after,
        String.join(
            ",",
            "inspectionConfig.sonarQubeInstallationName",
            "inspectionConfig.analysisStrategy.baseConfig.autoMatch",
            "inspectionConfig.analysisStrategy.baseConfig.projectPath",
            "inspectionConfig.analysisStrategy.baseConfig.sonarReportPath",
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
            "authConfig.httpCredentialsId"));
  }
}
