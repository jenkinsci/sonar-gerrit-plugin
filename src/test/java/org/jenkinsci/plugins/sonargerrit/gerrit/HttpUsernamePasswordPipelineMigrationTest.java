package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.queue.QueueTaskFuture;
import hudson.plugins.git.GitSCM;
import hudson.plugins.sonar.SonarBuildWrapper;
import hudson.security.ACL;
import hudson.tasks.Maven;
import hudson.util.Secret;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
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
class HttpUsernamePasswordPipelineMigrationTest {

  private static final String MAVEN_TARGET =
      "clean verify sonar:sonar "
          + "-Dsonar.pullrequest.key=${env.GERRIT_CHANGE_NUMBER}-${env.GERRIT_PATCHSET_NUMBER} "
          + "-Dsonar.pullrequest.base=${env.GERRIT_BRANCH} "
          + "-Dsonar.pullrequest.branch=${env.GERRIT_REFSPEC}";

  private static Cluster cluster;
  private static GerritGit git;

  @BeforeAll
  static void beforeAll(Cluster cluster, @TempDir Path workTree) throws Exception {

    HttpUsernamePasswordPipelineMigrationTest.cluster = cluster;

    git = GerritGit.createAndCloneRepository(cluster.gerrit(), workTree);

    git.addAndCommitFile(
        "pom.xml",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<project>\n"
            + "  <modelVersion>4.0.0</modelVersion>\n"
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
    triggerAndAssertSuccess(masterJob);
  }

  @BeforeEach
  void beforeEach() throws GitAPIException, IOException {
    git.resetToOriginMaster();

    SystemCredentialsProvider credentialsProvider = SystemCredentialsProvider.getInstance();
    credentialsProvider.getCredentials().removeIf(new PluginAuthoredCredentialsMatcher());
    credentialsProvider.save();
  }

  @Test
  @DisplayName("The migration creates only 2 credentials")
  void test1() throws Exception {
    GerritChange change = createChange();
    Job job = createPipelineJob(change);
    triggerAndAssertSuccess(job);
    assertCredentialsMigrated();
    triggerAndAssertSuccess(job);
    assertCredentialsMigrated();
  }

  private void assertCredentialsMigrated() {
    assertThat(listPluginAuthoredCredentials())
        .hasSize(2)
        .extracting(
            StandardUsernamePasswordCredentials::getUsername,
            StandardUsernamePasswordCredentials::getPassword)
        .containsExactlyInAnyOrder(
            tuple(
                cluster.gerrit().adminUsername(),
                Secret.fromString(cluster.gerrit().adminPassword())),
            tuple("", Secret.fromString(cluster.gerrit().adminPassword())));
  }

  private List<StandardUsernamePasswordCredentials> listPluginAuthoredCredentials() {
    return CredentialsMatchers.filter(
        CredentialsProvider.lookupCredentials(
            StandardUsernamePasswordCredentials.class,
            (Item) null,
            ACL.SYSTEM,
            Collections.emptyList()),
        new PluginAuthoredCredentialsMatcher());
  }

  private GerritChange createChange() throws GitAPIException, IOException, RestApiException {
    git.addAndCommitFile(
        "src/main/java/org/example/Foo.java",
        "package org.example; " + "public class Foo { " + "}");
    return git.createGerritChangeForMaster();
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
                cluster.jenkinsJdk17InstallationName(), cluster.jenkinsMavenInstallationName())
            + String.format("sh \"mvn %s\"\n", MAVEN_TARGET)
            + "}\n" // withMaven
            + "}\n" // withSonarQubeEnv
            + "} finally {\n"
            + "sonarToGerrit(\n"
            + String.format(
                "authConfig: [ username: '%s', password: '%s' ],\n",
                cluster.gerrit().adminUsername(), cluster.gerrit().adminPassword())
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

  private static class PluginAuthoredCredentialsMatcher
      implements CredentialsMatcher, Predicate<Credentials> {

    @Override
    public boolean matches(@NonNull Credentials item) {
      if (!(item instanceof StandardUsernamePasswordCredentials)) {
        return false;
      }
      StandardUsernamePasswordCredentials credentials = (StandardUsernamePasswordCredentials) item;
      return credentials.getId().startsWith("sonar-gerrit:");
    }

    @Override
    public boolean test(Credentials credentials) {
      return matches(credentials);
    }
  }
}
