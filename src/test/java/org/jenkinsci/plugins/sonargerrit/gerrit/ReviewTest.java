package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.queue.QueueTaskFuture;
import hudson.plugins.git.GitSCM;
import hudson.plugins.sonar.SonarBuildWrapper;
import hudson.tasks.Maven;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ChangeInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.CommentInfo;
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

/**
 * @author Réda Housni Alaoui
 */
@EnableCluster
class ReviewTest {

  private static final String MAVEN_TARGET =
      "clean verify sonar:sonar "
          + "-Dsonar.pullrequest.key=${env.GERRIT_CHANGE_NUMBER}-${env.GERRIT_PATCHSET_NUMBER} "
          + "-Dsonar.pullrequest.base=${env.GERRIT_BRANCH} "
          + "-Dsonar.pullrequest.branch=${env.GERRIT_REFSPEC}";
  private static final String FILEPATH =
      "child1/src/main/java/org/example/UselessConstructorDeclaration.java";
  private static final String S1186_VIOLATION =
      "package org.example; "
          + "public class UselessConstructorDeclaration { "
          + "public UselessConstructorDeclaration() {} "
          + "}";

  private static Cluster cluster;
  private static GerritGit git;

  @BeforeAll
  static void beforeAll(Cluster cluster, @TempDir Path workTree) throws Exception {

    ReviewTest.cluster = cluster;

    git = GerritGit.createAndCloneRepository(cluster.gerrit(), workTree);

    git.addAndCommitFile(
            "pom.xml",
            """
							<?xml version="1.0" encoding="UTF-8"?>
							<project>
							  <modelVersion>4.0.0</modelVersion>

							  <groupId>org.example</groupId>
							  <artifactId>example</artifactId>
							  <version>1.0-SNAPSHOT</version>
							  <packaging>pom</packaging>
							  <build>
							    <plugins>
							      <plugin>
							        <groupId>org.apache.maven.plugins</groupId>
							        <artifactId>maven-compiler-plugin</artifactId>
							        <version>3.12.1</version>
							      </plugin>
							    </plugins>
							    <pluginManagement>
							      <plugins>
							        <plugin>
							          <groupId>org.sonarsource.scanner.maven</groupId>
							          <artifactId>sonar-maven-plugin</artifactId>
							          <version>4.0.0.4121</version>
							        </plugin>
							      </plugins>
							    </pluginManagement>
							  </build>
							  <modules>
							    <module>child1</module>
							  </modules>
							</project>""")
        .addAndCommitFile(
            "child1/pom.xml",
            """
						<?xml version="1.0" encoding="UTF-8"?>
						<project>
						  <modelVersion>4.0.0</modelVersion>

						<parent>
						  <groupId>org.example</groupId>
						  <artifactId>example</artifactId>
						  <version>1.0-SNAPSHOT</version>
						</parent>

						  <artifactId>child1</artifactId>

						</project>""");

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
  void beforeEach() throws GitAPIException {
    git.resetToOriginMaster();
  }

  @Test
  @DisplayName("STANDARD comment type")
  void test1() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, false, false));

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
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.ROBOT, null, null, false, false));

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
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, false, false));

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
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.ROBOT, "/child2/**", null, false, false));

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
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.ROBOT, null, "/child2/**", false, false));

    ChangeInfo changeDetail = change.getDetail();
    assertThat(changeDetail.labels.get(GerritServer.CODE_QUALITY_LABEL).all)
        .hasSize(1)
        .map(approvalInfo -> approvalInfo.value)
        .containsExactly(-1);

    assertThat(change.listRobotComments())
        .filteredOn(comment -> comment.message.contains("S1186"))
        .hasSize(1);
  }

  @Test
  @DisplayName("Resolves the thread of an issue no longer reported")
  void test6() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, true, false));
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, "/child2/**", null, true, false));

    List<CommentInfo> comments = change.listComments();
    List<CommentInfo> issueComments =
        comments.stream().filter(comment -> comment.message.contains("S1186")).toList();
    assertThat(issueComments).hasSize(1);
    String issueCommentId = issueComments.get(0).id;

    assertThat(comments)
        .filteredOn(comment -> issueCommentId.equals(comment.inReplyTo))
        .extracting(comment -> comment.message, comment -> comment.unresolved)
        .containsExactly(tuple(StaleCommentResolver.RESOLUTION_MESSAGE, false));
  }

  @Test
  @DisplayName("Resolves the threads of older patch sets")
  void test7() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, true, false));
    git.addAndCommitFile(FILEPATH, S1186_VIOLATION + "\n", true);
    git.createGerritChangeForMaster();
    triggerAndAssertSuccess(
        createPipelineJob(change, 2, ReviewCommentType.STANDARD, null, null, true, false));

    List<CommentInfo> comments = change.listComments();
    Map<Integer, CommentInfo> issueCommentByPatchSet =
        comments.stream()
            .filter(comment -> comment.message.contains("S1186"))
            .collect(Collectors.toMap(comment -> comment.patchSet, Function.identity()));
    assertThat(issueCommentByPatchSet).containsOnlyKeys(1, 2);
    String patchSet1CommentId = issueCommentByPatchSet.get(1).id;
    CommentInfo patchSet2Comment = issueCommentByPatchSet.get(2);

    assertThat(comments)
        .filteredOn(comment -> patchSet1CommentId.equals(comment.inReplyTo))
        .extracting(comment -> comment.patchSet, comment -> comment.unresolved)
        .containsExactly(tuple(1, false));
    assertThat(patchSet2Comment.unresolved).isTrue();
    assertThat(comments).noneMatch(comment -> patchSet2Comment.id.equals(comment.inReplyTo));
  }

  @Test
  @DisplayName("Does not comment again an issue already commented on the patch set")
  void test8() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, true, false));
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, true, false));

    List<CommentInfo> comments = change.listComments();
    assertThat(comments).hasSize(1);
    assertThat(comments.get(0).message).contains("S1186");
    assertThat(comments.get(0).unresolved).isTrue();
  }

  @Test
  @DisplayName("Comments again an issue whose thread was resolved, despite duplicate omission")
  void test9() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, true, false));
    CommentInfo issueComment = change.listComments().get(0);
    ReviewInput.CommentInput resolvingReply = new ReviewInput.CommentInput();
    resolvingReply.inReplyTo = issueComment.id;
    resolvingReply.line = issueComment.line;
    resolvingReply.message = "Done";
    resolvingReply.unresolved = false;
    ReviewInput humanReview = new ReviewInput();
    humanReview.comments = Map.of(FILEPATH, List.of(resolvingReply));
    change.postReview(1, humanReview);

    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, true, true));

    List<CommentInfo> comments = change.listComments();
    assertThat(comments)
        .filteredOn(comment -> comment.message.contains("S1186"))
        .extracting(comment -> comment.inReplyTo, comment -> comment.unresolved)
        .containsExactlyInAnyOrder(tuple(null, true), tuple(null, true));
    assertThat(comments)
        .noneMatch(comment -> StaleCommentResolver.RESOLUTION_MESSAGE.equals(comment.message));
  }

  @Test
  @DisplayName("Leaves alone the unresolved threads not opened by the plugin")
  void test10() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, null, null, true, false));
    ReviewInput.CommentInput humanComment = new ReviewInput.CommentInput();
    humanComment.line = 1;
    humanComment.message = "Please check";
    humanComment.unresolved = true;
    ReviewInput humanReview = new ReviewInput();
    humanReview.comments = Map.of(FILEPATH, List.of(humanComment));
    change.postReview(1, humanReview);

    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, "/child2/**", null, true, false));

    List<CommentInfo> comments = change.listComments();
    List<CommentInfo> humanComments =
        comments.stream().filter(comment -> "Please check".equals(comment.message)).toList();
    assertThat(humanComments).hasSize(1);
    String humanCommentId = humanComments.get(0).id;
    assertThat(comments).noneMatch(comment -> humanCommentId.equals(comment.inReplyTo));
    assertThat(comments)
        .filteredOn(comment -> StaleCommentResolver.RESOLUTION_MESSAGE.equals(comment.message))
        .hasSize(1);
  }

  @Test
  @DisplayName("Leaves alone the threads of newer patch sets")
  void test11() throws Exception {
    GerritChange change = createChangeViolatingS1186();
    git.addAndCommitFile(FILEPATH, S1186_VIOLATION + "\n", true);
    git.createGerritChangeForMaster();
    triggerAndAssertSuccess(
        createPipelineJob(change, 2, ReviewCommentType.STANDARD, null, null, true, false));

    triggerAndAssertSuccess(
        createPipelineJob(change, 1, ReviewCommentType.STANDARD, "/child2/**", null, true, false));

    assertThat(change.listComments())
        .extracting(comment -> comment.patchSet, comment -> comment.unresolved)
        .containsExactly(tuple(2, true));
  }

  private GerritChange createChangeViolatingS1186()
      throws GitAPIException, IOException, RestApiException {
    git.addAndCommitFile(FILEPATH, S1186_VIOLATION);
    return git.createGerritChangeForMaster();
  }

  @SuppressWarnings("rawtypes")
  private Job createPipelineJob(
      GerritChange change,
      int patchSetNumber,
      ReviewCommentType commentType,
      String includedPathsGlobPattern,
      String excludedPathsGlobPattern,
      boolean resolveStaleComments,
      boolean omitDuplicateComments)
      throws IOException {
    WorkflowJob job = cluster.jenkinsRule().createProject(WorkflowJob.class);
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
                cluster.jenkinsJdk17InstallationName(), cluster.jenkinsMavenInstallationName())
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
            + String.format("resolveStaleComments: %s,\n", resolveStaleComments)
            + String.format("omitDuplicateComments: %s,\n", omitDuplicateComments)
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
    CpsFlowDefinition cpsFlowDefinition;
    try {
      cpsFlowDefinition = new CpsFlowDefinition(script, true);
    } catch (Descriptor.FormException e) {
      throw new RuntimeException(e);
    }
    job.setDefinition(cpsFlowDefinition);
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
