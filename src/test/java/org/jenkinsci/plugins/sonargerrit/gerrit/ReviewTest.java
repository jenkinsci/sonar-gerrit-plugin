package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.queue.QueueTaskFuture;
import hudson.plugins.git.GitSCM;
import hudson.plugins.sonar.SonarBuildWrapper;
import hudson.tasks.Maven;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ChangeInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster.Cluster;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster.EnableCluster;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritChange;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritGit;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritServer;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** @author Réda Housni Alaoui */
@EnableCluster
class ReviewTest {

  private static final String MAVEN_TARGET =
      "clean verify sonar:sonar "
          + "-Dsonar.pullrequest.key=${env.GERRIT_CHANGE_NUMBER}-${env.GERRIT_PATCHSET_NUMBER} "
          + "-Dsonar.pullrequest.base=${env.GERRIT_BRANCH} "
          + "-Dsonar.pullrequest.branch=${env.GERRIT_REFSPEC}";
  private static final String FILEPATH =
      "child1/src/main/java/org/example/UselessConstructorDeclaration.java";

  private static Cluster cluster;
  private static GerritGit git;

  @BeforeAll
  static void beforeAll(Cluster cluster, @TempDir Path workTree) throws Exception {

    ReviewTest.cluster = cluster;

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
                + "  <packaging>pom</packaging>"
                + "\n"
                + "<modules>\n"
                + "<module>child1</module>"
                + "</modules>"
                + "</project>")
        .addAndCommitFile(
            "child1/pom.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project>\n"
                + "  <modelVersion>4.0.0</modelVersion>\n"
                + "\n"
                + "<parent>\n"
                + "  <groupId>org.example</groupId>\n"
                + "  <artifactId>example</artifactId>\n"
                + "  <version>1.0-SNAPSHOT</version>\n"
                + "</parent>\n"
                + "\n"
                + "  <artifactId>child1</artifactId>\n"
                + "\n"
                + "</project>");

    git.push();

    FreeStyleProject masterJob = cluster.jenkinsRule().createFreeStyleProject();
    masterJob.setJDK(Jenkins.get().getJDK(cluster.jenkinsJdk8InstallationName()));
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
    triggerAndAssertSuccess(masterJob);
  }

  @BeforeEach
  void beforeEach() throws GitAPIException {
    git.resetToOriginMaster();
  }

  @Test
  @DisplayName("STANDARD comment type")
  void test1() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(createPipelineJob(change, ReviewCommentType.STANDARD, null, null));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(-1);

    assertThat(change.listComments())
        .filteredOn(comment -> comment.message.contains("S1186") && comment.unresolved)
        .hasSize(1);
  }

  @Test
  @DisplayName("ROBOT comment type")
  void test2() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(createPipelineJob(change, ReviewCommentType.ROBOT, null, null));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(-1);

    assertThat(change.listRobotComments())
        .filteredOn(comment -> comment.message.contains("S1186"))
        .hasSize(1)
        .anySatisfy(
            comment -> {
              assertThat(comment.robotId).isEqualTo("Sonar");
              assertThat(comment.robotRunId).isNotBlank();
              assertThat(comment.url).startsWith(cluster.sonarqube().url());
            });
  }

  @Test
  @DisplayName("Review tag is autogenerated:sonar")
  void test3() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(createPipelineJob(change, ReviewCommentType.STANDARD, null, null));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.tag)
        .containsExactly("autogenerated:sonar");
  }

  @Test
  @DisplayName("Issue ignored because of path glob pattern")
  void test4() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(createPipelineJob(change, ReviewCommentType.ROBOT, "/child2/**", null));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(1);

    assertThat(change.listRobotComments()).isEmpty();
  }

  @Test
  @DisplayName("Issue considered despite of path glob pattern")
  void test5() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(createPipelineJob(change, ReviewCommentType.ROBOT, null, "/child2/**"));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(-1);

    assertThat(change.listRobotComments())
        .filteredOn(comment -> comment.message.contains("S1186"))
        .hasSize(1);
  }

  private GerritChange createChangeViolatingS1186()
      throws GitAPIException, IOException, RestApiException {
    git.addAndCommitFile(
        FILEPATH,
        "package org.example; "
            + "public class UselessConstructorDeclaration { "
            + "public UselessConstructorDeclaration() {} "
            + "}");
    return git.createGerritChangeForMaster();
  }

  @SuppressWarnings("rawtypes")
  private Job createPipelineJob(
      GerritChange change,
      ReviewCommentType commentType,
      String includedPathsGlobPattern,
      String excludedPathsGlobPattern)
      throws IOException {
    WorkflowJob job = cluster.jenkinsRule().createProject(WorkflowJob.class);
    int patchSetNumber = 1;
    String quotedIncludedPathsGlobPattern =
        Optional.ofNullable(includedPathsGlobPattern)
            .map(s -> StringUtils.wrap(s, "'"))
            .orElse(null);

    String quotedExcludedPathsGlobPattern =
        Optional.ofNullable(excludedPathsGlobPattern)
            .map(s -> StringUtils.wrap(s, "'"))
            .orElse(null);
    String script =
        "node {\n"
            + "stage('Build') {\n"
            + "try {\n"
            + String.format("env.GERRIT_NAME = '%s'\n", cluster.jenkinsGerritTriggerServerName())
            + String.format("env.GERRIT_CHANGE_NUMBER = '%s'\n", change.changeNumericId())
            + String.format("env.GERRIT_PATCHSET_NUMBER = '%s'\n", patchSetNumber)
            + String.format("env.GERRIT_BRANCH = '%s'\n", "master")
            + String.format("env.GERRIT_REFSPEC = '%s'\n", change.refName(patchSetNumber))
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
            + String.format("sh \"mvn %s\"\n", MAVEN_TARGET)
            + "}\n" // withMaven
            + "}\n" // withSonarQubeEnv
            + "} finally {\n"
            + "sonarToGerrit(\n"
            + "inspectionConfig: [\n"
            + "analysisStrategy: pullRequest()\n"
            + "],\n" // inspectionConfig
            + "reviewConfig: [\n"
            + String.format("commentType: '%s',\n", commentType)
            + "issueFilterConfig: [\n"
            + "severity: 'MINOR',\n"
            + "newIssuesOnly: false,\n"
            + "changedLinesOnly: true,\n"
            + String.format("includedPathsGlobPattern: %s, \n", quotedIncludedPathsGlobPattern)
            + String.format("excludedPathsGlobPattern: %s\n", quotedExcludedPathsGlobPattern)
            + "]\n" // issueFilterConfig
            + "],\n" // reviewConfig
            + "scoreConfig: [\n"
            + "issueFilterConfig: [\n"
            + "severity: 'MINOR',\n"
            + "newIssuesOnly: false,\n"
            + "changedLinesOnly: true,\n"
            + String.format("includedPathsGlobPattern: %s,\n", quotedIncludedPathsGlobPattern)
            + String.format("excludedPathsGlobPattern: %s\n", quotedExcludedPathsGlobPattern)
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

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void triggerAndAssertSuccess(Job job) throws Exception {
    final QueueTaskFuture future =
        new ParameterizedJobMixIn() {
          @Override
          protected Job asJob() {
            return job;
          }
        }.scheduleBuild2(0);
    cluster.jenkinsRule().assertBuildStatusSuccess(future);
  }

  private static GitSCM createGitSCM() {
    return new GitSCM(
        GitSCM.createRepoList(git.httpUrl(), cluster.jenkinsGerritCredentialsId()),
        Collections.emptyList(),
        null,
        null,
        Collections.emptyList());
  }
}
