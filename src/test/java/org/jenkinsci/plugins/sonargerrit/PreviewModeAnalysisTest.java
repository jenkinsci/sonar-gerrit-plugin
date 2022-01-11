package org.jenkinsci.plugins.sonargerrit;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.queue.QueueTaskFuture;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.UserRemoteConfig;
import hudson.plugins.sonar.SonarBuildWrapper;
import hudson.tasks.Maven;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ChangeInfo;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.Inspection;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.PreviewModeAnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster.Cluster;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster.EnableCluster;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritChange;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritGit;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritServer;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnvironmentVariableBuildWrapper;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** @author Réda Housni Alaoui */
@EnableCluster
class PreviewModeAnalysisTest {

  private static final String MAVEN_TARGET =
      "clean verify sonar:sonar -Dsonar.analysis.mode=preview -Dsonar.report.export.path=sonar-report.json";

  private static Cluster cluster;
  private static GerritGit git;

  @BeforeAll
  static void beforeAll(Cluster cluster, @TempDir Path workTree)
      throws GitAPIException, IOException {

    PreviewModeAnalysisTest.cluster = cluster;

    git = GerritGit.createAndCloneRepository(cluster.gerrit(), workTree);

    git.addAndCommitFile(
        "pom.xml",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<project>\n"
            + "  <modelVersion>4.0.0</modelVersion>\n"
            + "\n"
            + "  <groupId>org.example</groupId>\n"
            + "  <artifactId>example</artifactId>\n"
            + "  <version>1.0-SNAPSHOT</version>\n"
            + "</project>");

    git.push();
  }

  @BeforeEach
  void beforeEach() throws GitAPIException {
    git.resetToOriginMaster();
  }

  @Test
  @DisplayName("Bad quality freestyle build")
  void test1() throws Exception {
    testWithBadQualityCode(this::createFreestyleJob);
  }

  @Test
  @DisplayName("Good quality freestyle build")
  void test2() throws Exception {
    testWithGoodQualityCode(this::createFreestyleJob);
  }

  @Test
  @DisplayName("Bad quality pipeline build")
  void test3() throws Exception {
    testWithBadQualityCode(this::createPipelineJob);
  }

  @Test
  @DisplayName("Good quality pipeline build")
  void test4() throws Exception {
    testWithGoodQualityCode(this::createPipelineJob);
  }

  private void testWithBadQualityCode(JobFactory jobFactory) throws Exception {
    git.addAndCommitFile(
        "src/main/java/org/example/UselessConstructorDeclaration.java",
        "package org.example; "
            + "public class UselessConstructorDeclaration { "
            + "public UselessConstructorDeclaration() {} "
            + "}");
    GerritChange change = git.createGerritChangeForMaster();

    triggerAndAssertSuccess(jobFactory.build(change));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(-1);
    assertThat(change.listComments())
        .map(commentInfo -> commentInfo.message)
        .filteredOn(message -> message.contains("S1186"))
        .hasSize(1);
  }

  private void testWithGoodQualityCode(JobFactory jobFactory) throws Exception {
    git.addAndCommitFile(
        "src/main/java/org/example/Foo.java", "package org.example; public interface Foo {}");
    GerritChange change = git.createGerritChangeForMaster();

    triggerAndAssertSuccess(jobFactory.build(change));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(1);
    assertThat(change.listComments()).isEmpty();
  }

  @SuppressWarnings("rawtypes")
  private Job createFreestyleJob(GerritChange change) throws IOException {
    FreeStyleProject job = cluster.jenkinsRule().createFreeStyleProject();
    job.setJDK(Jenkins.get().getJDK(cluster.jenkinsJdk8InstallationName()));

    int patchSetNumber = 1;
    job.setScm(createGitSCM(change, patchSetNumber));

    job.getBuildWrappersList()
        .add(new SonarBuildWrapper(cluster.jenkinsSonarqubeInstallationName()));

    job.getBuildWrappersList()
        .add(
            new EnvironmentVariableBuildWrapper()
                .add("GERRIT_NAME", cluster.jenkinsGerritTriggerServerName())
                .add("GERRIT_CHANGE_NUMBER", change.changeNumericId())
                .add("GERRIT_PATCHSET_NUMBER", String.valueOf(patchSetNumber)));

    job.getBuildersList().add(new Maven(MAVEN_TARGET, cluster.jenkinsMavenInstallationName()));

    SonarToGerritPublisher sonarToGerrit = new SonarToGerritPublisher();
    Inspection inspectionConfig = sonarToGerrit.getInspectionConfig();
    PreviewModeAnalysisStrategy analysisStrategy = new PreviewModeAnalysisStrategy();
    analysisStrategy.setSonarQubeInstallationName(cluster.jenkinsSonarqubeInstallationName());
    analysisStrategy.setAutoMatch(true);
    inspectionConfig.setAnalysisStrategy(analysisStrategy);
    IssueFilterConfig issueFilterConfig = sonarToGerrit.getReviewConfig().getIssueFilterConfig();
    issueFilterConfig.setSeverity("MINOR");
    issueFilterConfig.setChangedLinesOnly(true);

    ScoreConfig scoreConfig = new ScoreConfig();
    scoreConfig.getIssueFilterConfig().setSeverity("MINOR");
    scoreConfig.getIssueFilterConfig().setNewIssuesOnly(false);
    scoreConfig.getIssueFilterConfig().setChangedLinesOnly(true);
    scoreConfig.setCategory(GerritServer.CODE_QUALITY_LABEL);
    scoreConfig.setNoIssuesScore(1);
    scoreConfig.setIssuesScore(-1);
    sonarToGerrit.setScoreConfig(scoreConfig);
    job.getPublishersList().add(sonarToGerrit);
    return job;
  }

  @SuppressWarnings("rawtypes")
  private Job createPipelineJob(GerritChange change) throws IOException {
    WorkflowJob job = cluster.jenkinsRule().createProject(WorkflowJob.class);
    int patchSetNumber = 1;
    String script =
        "node {\n"
            + "stage('Build') {\n"
            + "try {\n"
            + String.format("env.GERRIT_NAME = '%s'\n", cluster.jenkinsGerritTriggerServerName())
            + String.format("env.GERRIT_CHANGE_NUMBER = '%s'\n", change.changeNumericId())
            + String.format("env.GERRIT_PATCHSET_NUMBER = '%s'\n", patchSetNumber)
            + "checkout scm: ([\n"
            + "$class: 'GitSCM',\n"
            + String.format(
                "userRemoteConfigs: [[url: '%s', refspec: '%s', credentialsId: '%s']],\n",
                git.httpUrl(), change.refName(patchSetNumber), cluster.jenkinsGerritCredentialsId())
            + "branches: [[name: 'FETCH_HEAD']]\n"
            + "])\n"
            + String.format(
                "withSonarQubeEnv('%s') {\n", cluster.jenkinsSonarqubeInstallationName())
            + String.format(
                "withMaven(jdk: '%s', maven: '%s') {\n",
                cluster.jenkinsJdk8InstallationName(), cluster.jenkinsMavenInstallationName())
            + String.format("sh 'mvn %s'\n", MAVEN_TARGET)
            + "}\n" // withMaven
            + "}\n" // withSonarQubeEnv
            + "} finally {\n"
            + "sonarToGerrit(\n"
            + "inspectionConfig: [\n"
            + "analysisStrategy: previewMode(\n"
            + String.format(
                "sonarQubeInstallationName: '%s',\n", cluster.jenkinsSonarqubeInstallationName())
            + "baseConfig: [autoMatch: true]\n"
            + ")\n"
            + "],\n" // inspectionConfig
            + "reviewConfig: [\n"
            + "issueFilterConfig: [\n"
            + "severity: 'MINOR',\n"
            + "newIssuesOnly: false,\n"
            + "changedLinesOnly: true\n"
            + "]\n" // issueFilterConfig
            + "],\n" // reviewConfig
            + "scoreConfig: [\n"
            + "issueFilterConfig: [\n"
            + "severity: 'MINOR',"
            + "newIssuesOnly: false,"
            + "changedLinesOnly: true"
            + "],\n" // issueFilterConfig
            + String.format("category: '%s',\n", GerritServer.CODE_QUALITY_LABEL)
            + "noIssuesScore: 1,\n"
            + "issuesScore: -1,\n"
            + "]\n" // scoreConfig
            + ")\n" // sonarToGerrit
            + "}\n" // finally
            + "}\n" // stage('Build')
            + "}";
    job.setDefinition(new CpsFlowDefinition(script, true));
    return job;
  }

  private GitSCM createGitSCM(GerritChange change, int patchSetNumber) {
    String refName = change.refName(patchSetNumber);
    List<UserRemoteConfig> remoteConfigs =
        Collections.singletonList(
            new UserRemoteConfig(
                git.httpUrl(), null, refName, cluster.jenkinsGerritCredentialsId()));
    return new GitSCM(
        remoteConfigs,
        Collections.singletonList(new BranchSpec("FETCH_HEAD")),
        null,
        null,
        Collections.emptyList());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void triggerAndAssertSuccess(Job job) throws Exception {
    final QueueTaskFuture future =
        new ParameterizedJobMixIn() {
          @Override
          protected Job asJob() {
            return job;
          }
        }.scheduleBuild2(0);
    cluster.jenkinsRule().assertBuildStatusSuccess(future);
  }

  private interface JobFactory {
    @SuppressWarnings("rawtypes")
    Job build(GerritChange change) throws Exception;
  }
}
