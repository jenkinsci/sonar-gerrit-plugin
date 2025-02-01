package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.model.FreeStyleProject;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.UserRemoteConfig;
import hudson.plugins.sonar.SonarBuildWrapper;
import hudson.tasks.Maven;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import jenkins.model.Jenkins;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ChangeInfo;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.Inspection;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilterConfig;
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
class PullRequestAnalysisTest {

  private static final String MAVEN_FREESTYLE_TARGET =
      "clean verify sonar:sonar "
          + "-Dsonar.pullrequest.key=${GERRIT_CHANGE_NUMBER} "
          + "-Dsonar.pullrequest.base=${GERRIT_BRANCH} "
          + "-Dsonar.pullrequest.branch=${GERRIT_REFSPEC}";

  private static final String MAVEN_PIPELINE_TARGET =
      "clean verify sonar:sonar "
          + "-Dsonar.pullrequest.key=${env.GERRIT_CHANGE_NUMBER} "
          + "-Dsonar.pullrequest.base=${env.GERRIT_BRANCH} "
          + "-Dsonar.pullrequest.branch=${env.GERRIT_REFSPEC}";

  private static Cluster cluster;
  private static GerritGit git;

  @BeforeAll
  static void beforeAll(Cluster cluster, @TempDir Path workTree) throws Exception {

    PullRequestAnalysisTest.cluster = cluster;

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
            + "  <build>\n"
            + "    <plugins>\n"
            + "      <plugin>\n"
            + "        <groupId>org.apache.maven.plugins</groupId>\n"
            + "        <artifactId>maven-compiler-plugin</artifactId>\n"
            + "        <version>3.12.1</version>\n"
            + "      </plugin>\n"
            + "    </plugins>\n"
            + "  </build>\n"
            + "</project>");

    git.push();

    FreeStyleProject masterJob = cluster.jenkinsRule().createFreeStyleProject();
    masterJob.setJDK(Jenkins.get().getJDK(cluster.jenkinsJdk17InstallationName()));
    masterJob.setScm(createGitSCM());
    masterJob
        .getBuildWrappersList()
        .add(new SonarBuildWrapper(cluster.jenkinsSonarqubeInstallationName()));
    masterJob
        .getBuildersList()
        .add(
            new Maven(
                "clean verify sonar:sonar -Dsonar.branch.name=master",
                cluster.jenkinsMavenInstallationName()));
    cluster.jenkinsRule().buildAndAssertSuccess(masterJob);
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
    testWithGoodQualityCode(this::createFreestyleJob, false);
  }

  @Test
  @DisplayName("Bad then good quality freestyle build")
  void test3() throws Exception {
    testWithBadQualityCode(this::createFreestyleJob);
    testWithGoodQualityCode(this::createFreestyleJob, true);
  }

  @Test
  @DisplayName("Bad quality pipeline build")
  void test4() throws Exception {
    testWithBadQualityCode(this::createPipelineJob);
  }

  @Test
  @DisplayName("Good quality pipeline build")
  void test5() throws Exception {
    testWithGoodQualityCode(this::createPipelineJob, false);
  }

  @Test
  @DisplayName("Bad then good quality pipeline build")
  void test6() throws Exception {
    testWithBadQualityCode(this::createPipelineJob);
    testWithGoodQualityCode(this::createFreestyleJob, true);
  }

  private void testWithBadQualityCode(JobFactory jobFactory) throws Exception {
    git.addAndCommitFile(
        "src/main/java/org/example/Foo.java",
        "package org.example; public class Foo { public Foo() {} }");
    GerritChange change = git.createGerritChangeForMaster();

    int patchSetNumber = 1;
    jobFactory.createAndBuild(change, patchSetNumber);

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(-1);
    assertThat(change.listComments())
        .filteredOn(
            comment -> comment.patchSet == patchSetNumber && comment.message.contains("S1186"))
        .hasSize(1);
  }

  private void testWithGoodQualityCode(JobFactory jobFactory, boolean amend) throws Exception {
    git.addAndCommitFile(
        "src/main/java/org/example/Foo.java",
        "package org.example; public interface Foo {}",
        amend);
    GerritChange change = git.createGerritChangeForMaster();

    int patchSetNumber = amend ? 2 : 1;
    jobFactory.createAndBuild(change, patchSetNumber);

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(1);
    assertThat(change.listComments())
        .filteredOn(comment -> comment.patchSet == patchSetNumber)
        .isEmpty();
  }

  private void createFreestyleJob(GerritChange change, int patchSetNumber) throws Exception {
    FreeStyleProject job = cluster.jenkinsRule().createFreeStyleProject();
    job.setJDK(Jenkins.get().getJDK(cluster.jenkinsJdk17InstallationName()));

    job.setScm(createGitSCM(change, patchSetNumber));

    job.getBuildWrappersList()
        .add(new SonarBuildWrapper(cluster.jenkinsSonarqubeInstallationName()));

    job.getBuildWrappersList()
        .add(
            new EnvironmentVariableBuildWrapper()
                .add("GERRIT_NAME", cluster.jenkinsGerritTriggerServerName())
                .add("GERRIT_CHANGE_NUMBER", change.changeNumericId())
                .add("GERRIT_PATCHSET_NUMBER", String.valueOf(patchSetNumber))
                .add("GERRIT_BRANCH", "master")
                .add("GERRIT_REFSPEC", change.refName(patchSetNumber)));

    job.getBuildersList()
        .add(new Maven(MAVEN_FREESTYLE_TARGET, cluster.jenkinsMavenInstallationName()));

    SonarToGerritPublisher sonarToGerrit = new SonarToGerritPublisher();
    Inspection inspectionConfig = sonarToGerrit.getInspectionConfig();
    inspectionConfig.setAnalysisStrategy(new PullRequestAnalysisStrategy());
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
    cluster.jenkinsRule().buildAndAssertSuccess(job);
  }

  private void createPipelineJob(GerritChange change, int patchSetNumber) throws Exception {
    WorkflowJob job = cluster.jenkinsRule().createProject(WorkflowJob.class);
    String script =
        "node {\n"
            + "stage('Build') {\n"
            + "try {\n"
            + String.format("env.GERRIT_NAME = '%s'%n", cluster.jenkinsGerritTriggerServerName())
            + String.format("env.GERRIT_CHANGE_NUMBER = '%s'%n", change.changeNumericId())
            + String.format("env.GERRIT_PATCHSET_NUMBER = '%s'%n", patchSetNumber)
            + String.format("env.GERRIT_BRANCH = '%s'%n", "master")
            + String.format("env.GERRIT_REFSPEC = '%s'%n", change.refName(patchSetNumber))
            + "checkout scm: ([\n"
            + "$class: 'GitSCM',\n"
            + String.format(
                "userRemoteConfigs: [[url: '%s', refspec: '%s', credentialsId: '%s']],%n",
                git.httpUrl(), change.refName(patchSetNumber), cluster.jenkinsGerritCredentialsId())
            + "branches: [[name: 'FETCH_HEAD']]\n"
            + "])\n"
            + String.format(
                "withSonarQubeEnv('%s') {%n", cluster.jenkinsSonarqubeInstallationName())
            + String.format(
                "withMaven(jdk: '%s', maven: '%s') {%n",
                cluster.jenkinsJdk17InstallationName(), cluster.jenkinsMavenInstallationName())
            + String.format("sh \"mvn %s\"%n", MAVEN_PIPELINE_TARGET)
            + "}\n" // withMaven
            + "}\n" // withSonarQubeEnv
            + "} finally {\n"
            + "sonarToGerrit(\n"
            + "inspectionConfig: [\n"
            + "analysisStrategy: pullRequest()\n"
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
            + String.format("category: '%s',%n", GerritServer.CODE_QUALITY_LABEL)
            + "noIssuesScore: 1,\n"
            + "issuesScore: -1,\n"
            + "]\n" // scoreConfig
            + ")\n" // sonarToGerrit
            + "}\n" // finally
            + "}\n" // stage('Build')
            + "}";
    job.setDefinition(new CpsFlowDefinition(script, true));
    cluster.jenkinsRule().buildAndAssertSuccess(job);
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

  private static GitSCM createGitSCM() {
    return new GitSCM(
        GitSCM.createRepoList(git.httpUrl(), cluster.jenkinsGerritCredentialsId()),
        Collections.emptyList(),
        null,
        null,
        Collections.emptyList());
  }

  private interface JobFactory {
    void createAndBuild(GerritChange change, int patchSetNumber) throws Exception;
  }
}
