package org.jenkinsci.plugins.sonargerrit;

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritAuthenticationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ReviewCommentType;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.PreviewModeAnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis.PullRequestAnalysisStrategy;
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

    GerritAuthenticationConfig authenticationConfig = new GerritAuthenticationConfig();
    authenticationConfig.setUsername("john.doe");
    authenticationConfig.setSecretPassword(Secret.fromString("secret"));
    before.setAuthConfig(authenticationConfig);

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

    GerritAuthenticationConfig authenticationConfig = new GerritAuthenticationConfig();
    authenticationConfig.setHttpCredentialsId(UUID.randomUUID().toString());
    before.setAuthConfig(authenticationConfig);

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
  @DisplayName("361.v3f45367a_71da_")
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

    GerritAuthenticationConfig authenticationConfig = new GerritAuthenticationConfig();
    authenticationConfig.setHttpCredentialsId(UUID.randomUUID().toString());
    before.setAuthConfig(authenticationConfig);

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

  @Test
  @DisplayName("Preview mode analysis next")
  void test4(JenkinsRule jenkinsRule) throws Exception {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();

    SonarToGerritPublisher before = new SonarToGerritPublisher();
    project.getPublishersList().add(before);

    PreviewModeAnalysisStrategy previewModeAnalysisStrategy = new PreviewModeAnalysisStrategy();
    before.getInspectionConfig().setAnalysisStrategy(previewModeAnalysisStrategy);
    previewModeAnalysisStrategy.setSonarQubeInstallationName(UUID.randomUUID().toString());
    previewModeAnalysisStrategy.setBaseConfig(new SubJobConfig());
    previewModeAnalysisStrategy.getBaseConfig().setAutoMatch(true);
    previewModeAnalysisStrategy.getBaseConfig().setProjectPath("foo");
    previewModeAnalysisStrategy.getBaseConfig().setSonarReportPath("bar");

    before.getReviewConfig().setCommentType(ReviewCommentType.ROBOT);
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

    GerritAuthenticationConfig authenticationConfig = new GerritAuthenticationConfig();
    authenticationConfig.setHttpCredentialsId(UUID.randomUUID().toString());
    before.setAuthConfig(authenticationConfig);

    jenkinsRule.submit(
        jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

    SonarToGerritPublisher after = project.getPublishersList().get(SonarToGerritPublisher.class);
    jenkinsRule.assertEqualBeans(
        before,
        after,
        String.join(
            ",",
            "inspectionConfig.analysisStrategy.sonarQubeInstallationName",
            "inspectionConfig.analysisStrategy.baseConfig.autoMatch",
            "inspectionConfig.analysisStrategy.baseConfig.projectPath",
            "inspectionConfig.analysisStrategy.baseConfig.sonarReportPath",
            "reviewConfig.commentType",
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
  @DisplayName("Pull request analysis next")
  void test5(JenkinsRule jenkinsRule) throws Exception {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();

    SonarToGerritPublisher before = new SonarToGerritPublisher();
    project.getPublishersList().add(before);

    before.getInspectionConfig().setAnalysisStrategy(new PullRequestAnalysisStrategy());

    before.getReviewConfig().setCommentType(ReviewCommentType.ROBOT);
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

    GerritAuthenticationConfig authenticationConfig = new GerritAuthenticationConfig();
    authenticationConfig.setHttpCredentialsId(UUID.randomUUID().toString());
    before.setAuthConfig(authenticationConfig);

    jenkinsRule.submit(
        jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

    SonarToGerritPublisher after = project.getPublishersList().get(SonarToGerritPublisher.class);
    jenkinsRule.assertEqualBeans(
        before,
        after,
        String.join(
            ",",
            "inspectionConfig.analysisStrategy",
            "reviewConfig.commentType",
            "reviewConfig.issueFilterConfig.changedLinesOnly",
            "reviewConfig.issueFilterConfig.newIssuesOnly",
            "reviewConfig.issueFilterConfig.severity",
            "reviewConfig.issueFilterConfig.includedPathsGlobPattern",
            "reviewConfig.issueFilterConfig.excludedPathsGlobPattern",
            "scoreConfig.issueFilterConfig.changedLinesOnly",
            "scoreConfig.issueFilterConfig.newIssuesOnly",
            "scoreConfig.issueFilterConfig.severity",
            "scoreConfig.issueFilterConfig.includedPathsGlobPattern",
            "scoreConfig.issueFilterConfig.excludedPathsGlobPattern",
            "scoreConfig.noIssuesScore",
            "scoreConfig.issuesScore",
            "scoreConfig.category",
            "notificationConfig.noIssuesNotificationRecipient",
            "notificationConfig.commentedIssuesNotificationRecipient",
            "notificationConfig.negativeScoreNotificationRecipient",
            "authConfig.httpCredentialsId"));
  }
}
