package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import static java.util.Objects.requireNonNull;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;
import hudson.plugins.sonar.action.SonarAnalysisAction;
import hudson.plugins.sonar.utils.SonarUtils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.redaalaoui.org.sonarqube.ws.Ce;
import me.redaalaoui.org.sonarqube.ws.Issues;
import me.redaalaoui.org.sonarqube.ws.ProjectPullRequests;
import me.redaalaoui.org.sonarqube.ws.client.HttpConnector;
import me.redaalaoui.org.sonarqube.ws.client.WsClient;
import me.redaalaoui.org.sonarqube.ws.client.WsClientFactories;
import me.redaalaoui.org.sonarqube.ws.client.ce.CeService;
import me.redaalaoui.org.sonarqube.ws.client.ce.TaskRequest;
import me.redaalaoui.org.sonarqube.ws.client.issues.SearchRequest;
import me.redaalaoui.org.sonarqube.ws.client.projectpullrequests.ListRequest;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.sonar.Components;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.SonarInstallationAdditionalAnalysisProperties;

/** @author Réda Housni Alaoui */
class PullRequestAnalysisTask {

  private static final String PLEASE_USE_THE_WITH_SONAR_QUBE_ENV_WRAPPER_TO_RUN_YOUR_ANALYSIS =
      "Please use the 'withSonarQubeEnv' wrapper to run your analysis.";
  private static final Duration CE_TASK_COMPLETION_CHECK_INTERVAL = Duration.ofSeconds(5);

  private final WsClient sonarClient;
  private final String serverUrl;
  private final String taskId;
  private final String componentKey;

  private PullRequestAnalysisTask(
      WsClient sonarClient, String serverUrl, String taskId, String componentKey) {
    this.sonarClient = requireNonNull(sonarClient);
    this.serverUrl = requireNonNull(StringUtils.trimToNull(serverUrl));
    this.taskId = requireNonNull(StringUtils.trimToNull(taskId));
    this.componentKey = requireNonNull(StringUtils.trimToNull(componentKey));
  }

  public static PullRequestAnalysisTask parseLastAnalysis(Run<?, ?> run) {
    List<SonarAnalysisAction> actions = run.getActions(SonarAnalysisAction.class);
    if (actions.isEmpty()) {
      throw new IllegalStateException(
          String.format(
              "No previous SonarQube analysis found on this build. %s",
              PLEASE_USE_THE_WITH_SONAR_QUBE_ENV_WRAPPER_TO_RUN_YOUR_ANALYSIS));
    }

    // Consider last analysis first
    List<SonarAnalysisAction> reversedActions = new ArrayList<>(actions);
    Collections.reverse(reversedActions);
    return reversedActions.stream()
        .map(sonarAnalysisAction -> tryCreate(run, sonarAnalysisAction))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    String.format(
                        "No previous SonarQube pull request analysis found on this build. %s",
                        PLEASE_USE_THE_WITH_SONAR_QUBE_ENV_WRAPPER_TO_RUN_YOUR_ANALYSIS)));
  }

  private static Optional<PullRequestAnalysisTask> tryCreate(
      Run<?, ?> run, SonarAnalysisAction action) {
    String ceTaskId = action.getCeTaskId();
    if (ceTaskId == null) {
      return Optional.empty();
    }
    String serverUrl = action.getServerUrl();
    if (serverUrl == null) {
      return Optional.empty();
    }
    String installationName = action.getInstallationName();
    SonarInstallation sonarInstallation =
        Optional.ofNullable(SonarInstallation.get(installationName))
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        String.format("Invalid installation name: %s", installationName)));

    String authenticationToken =
        SonarUtils.getAuthenticationToken(
            run, sonarInstallation, sonarInstallation.getCredentialsId());

    HttpConnector.Builder httpConnectorBuilder =
        HttpConnector.newBuilder().url(serverUrl).token(authenticationToken);

    httpConnectorBuilder =
        SonarInstallationAdditionalAnalysisProperties.parse(sonarInstallation)
            .getDuration("sonar.ws.timeout", ChronoUnit.SECONDS)
            .map(Duration::toMillis)
            .map(Long::intValue)
            .map(httpConnectorBuilder::readTimeoutMilliseconds)
            .orElse(httpConnectorBuilder);

    WsClient sonarClient = WsClientFactories.getDefault().newClient(httpConnectorBuilder.build());

    Ce.Task ceTask = sonarClient.ce().task(new TaskRequest().setId(ceTaskId)).getTask();
    String componentKey = ceTask.getComponentKey();
    if (StringUtils.isBlank(componentKey)) {
      return Optional.empty();
    }

    return Optional.of(new PullRequestAnalysisTask(sonarClient, serverUrl, ceTaskId, componentKey));
  }

  public List<Issue> fetchIssues(TaskListener listener) throws InterruptedException {
    CeService ceService = sonarClient.ce();
    Ce.Task ceTask;
    while (true) {
      ceTask = ceService.task(new TaskRequest().setId(taskId)).getTask();
      if (isComplete(listener, ceTask)) {
        break;
      }
      TaskListenerLogger.log(
          listener,
          "Waiting %s before re-checking SonarQube task '%s' status ...",
          CE_TASK_COMPLETION_CHECK_INTERVAL,
          taskId);
      Thread.sleep(CE_TASK_COMPLETION_CHECK_INTERVAL.toMillis());
    }

    String pullRequestKey = ceTask.getPullRequest();
    if (StringUtils.isBlank(pullRequestKey)) {
      // Sometimes, when the task fails very early, the pull request attribute is blank.
      // This is why, we can't control the presence of this attribute before creating the
      // PullRequestAnalysisTask.
      throw new IllegalStateException(
          String.format("No pull request found for SonarQube task '%s'", taskId));
    }

    ProjectPullRequests.PullRequest pullRequest;
    while (true) {
      pullRequest =
          sonarClient.projectPullRequests().list(new ListRequest().setProject(componentKey))
              .getPullRequestsList().stream()
              .filter(pr -> pullRequestKey.equals(pr.getKey()))
              .findFirst()
              .orElse(null);
      if (pullRequest != null) {
        break;
      }
      TaskListenerLogger.log(
          listener,
          "Waiting %s before re-performing lookup of pull request '%s' on project '%s' ...",
          CE_TASK_COMPLETION_CHECK_INTERVAL,
          pullRequestKey,
          componentKey);
      Thread.sleep(CE_TASK_COMPLETION_CHECK_INTERVAL.toMillis());
    }

    SearchRequest issueSearchRequest =
        new SearchRequest()
            .setResolved("false")
            .setComponentKeys(Collections.singletonList(componentKey))
            .setPullRequest(pullRequestKey);

    Issues.SearchWsResponse issueSearchResponse = sonarClient.issues().search(issueSearchRequest);

    Components components =
        new Components(
            issueSearchResponse.getComponentsList().stream()
                .map(PullRequestComponent::new)
                .collect(Collectors.toList()));

    ProjectPullRequests.PullRequest finalPullRequest = pullRequest;

    return issueSearchResponse.getIssuesList().stream()
        .map(issue -> new PullRequestIssue(finalPullRequest, components, issue, serverUrl))
        .collect(Collectors.toList());
  }

  private boolean isComplete(TaskListener listener, Ce.Task ceTask) {
    Ce.TaskStatus taskStatus = ceTask.getStatus();
    switch (taskStatus) {
      case SUCCESS:
        TaskListenerLogger.log(listener, "SonarQube task '%s' completed.", taskId);
        return true;
      case PENDING:
        TaskListenerLogger.log(listener, "SonarQube task '%s' is pending.", taskId);
        return false;
      case IN_PROGRESS:
        TaskListenerLogger.log(listener, "SonarQube task '%s' is in progress.", taskId);
        return false;
      case FAILED:
        throw new IllegalStateException(
            String.format(
                "SonarQube analysis '%s' failed with message: %s.",
                taskId, ceTask.getErrorMessage()));
      case CANCELED:
        throw new IllegalStateException(
            String.format("SonarQube analysis '%s' was canceled.", taskId));
      default:
        throw new IllegalStateException(
            String.format(
                "SonarQube analysis '%s' returned unexpected status '%s'", taskId, taskStatus));
    }
  }
}
