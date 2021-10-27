package org.jenkinsci.plugins.sonargerrit.sonar;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.google.common.base.Strings;
import hudson.AbortException;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;
import java.io.PrintStream;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.sonar.dto.ComponentSearchResult;
import org.jenkinsci.plugins.sonargerrit.sonar.dto.ComponentTasks;
import org.jenkinsci.plugins.sonargerrit.sonar.dto.PullRequestListResult;
import org.jenkinsci.plugins.sonargerrit.sonar.dto.Task;
import org.jenkinsci.plugins.sonargerrit.util.Localization;

public class SonarClient implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SonarClient.class.getName());
    private static final int PAGE_SIZE = 500;

    private static final Duration PULL_REQUEST_EXISTENCE_CHECK_INTERVAL = Duration.ofSeconds(5);

    private final String serverUrl;
    private final Client client;

    @Nullable
    private final TaskListener taskListener;


    public SonarClient(SonarInstallation sonarInstallation, StringCredentials credentials, @Nullable TaskListener taskListener)
            throws AbortException {
        this.serverUrl = sonarInstallation.getServerUrl();
        this.taskListener = taskListener;

        if (credentials == null) {
            throw new AbortException(Localization.getLocalized("jenkins.plugin.error.sonar.server.authmissing"));
        }
        String token = credentials.getSecret().getPlainText();
        HttpAuthenticationFeature basicAuth = HttpAuthenticationFeature.basic(token, "");

        client = ClientBuilder.newClient();
        client.register(basicAuth);
    }

    public void waitForPullRequestAnalysisCompletion(String component, String pullRequestKey, PrintStream logger) throws InterruptedException {
        while (true){
            if (fetchQueuedTask(component, pullRequestKey) == null) {
                return;
            }

            logger.println(getLocalized( "jenkins.plugin.sonar.issues.interval-wait", PULL_REQUEST_EXISTENCE_CHECK_INTERVAL));
            Thread.sleep(PULL_REQUEST_EXISTENCE_CHECK_INTERVAL.toMillis());
        }
    }

    private Task fetchQueuedTask(String component, String pullRequestKey) {
        List<Task> queue = client.target(serverUrl)
                .path("api")
                .path("ce")
                .path("component")
                .queryParam("component", component)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(ComponentTasks.class)
                .getQueue();
        if (queue == null) {
            return null;
        }
        return queue.stream()
                .filter(task -> pullRequestKey.equals(task.getPullRequest()))
                .findFirst()
                .orElse(null);
    }

    public Report fetchIssues(String component, String pullRequestKey) throws AbortException {
        if (StringUtils.isBlank(component)) {
            throw new AbortException("SonarQube component is missing");
        }
        if (StringUtils.isBlank(pullRequestKey)) {
            throw new AbortException("SonarQube pullRequestKey is missing");
        }
        if (!isPullRequestExists(component, pullRequestKey)) {
            throw new AbortException("Could not find pull request for project '" + component + "' and pull request key '" + pullRequestKey + "'");
        }

        WebTarget target = client.target(serverUrl).path("api").path("issues").path("search")
                .queryParam("componentKeys", component)
                .queryParam("pullRequest", pullRequestKey);

        TaskListenerLogger.logMessage(taskListener, LOGGER, Level.INFO, "jenkins.plugin.sonar.fetch", target.getUri());

        Report report = target.request(MediaType.APPLICATION_JSON_TYPE).get(Report.class);
        // Pull Request Analysis only reports new issues, attribute is not present in JSON response
        report.getIssues().forEach(issue -> issue.setNew(true));

        TaskListenerLogger.logMessage(taskListener, LOGGER, Level.INFO, "jenkins.plugin.sonar.report", report.getIssues().size());

        return report;
    }

    /**
     * @see  <a href="https://sonarqube.mamdev.server.lan/web_api/api/components">https://sonarqube.mamdev.server.lan/web_api/api/components</a>
     *
     * @param component name of component which should be fetched
     * @return result matching the component name
     */
    public ComponentSearchResult fetchComponents(@Nullable String component) {
        ComponentSearchResult componentSearchResult = null;
        Integer total = null;

        for (int currentPage = 1; total == null || (currentPage - 1) * PAGE_SIZE < total; currentPage++) {
            ComponentSearchResult pageResult = fetchComponents(component, currentPage);
            if (componentSearchResult == null) {
                componentSearchResult = pageResult;
                total = pageResult.getPaging().getTotal();
            } else {
                componentSearchResult.getComponents().addAll(pageResult.getComponents());
            }
        }

        return componentSearchResult;
    }

    private ComponentSearchResult fetchComponents(@Nullable String component, int page) {
        WebTarget target = client.target(serverUrl).path("api").path("components").path("search")
                .queryParam("qualifiers", "TRK") // TRK - Projects
                .queryParam("ps", PAGE_SIZE) // page size
                .queryParam("p", page); // 1-based page number

        if (!Strings.isNullOrEmpty(component)) {
            target = target.queryParam("q", component); // component key
        }

        return target.request(MediaType.APPLICATION_JSON_TYPE).get(ComponentSearchResult.class);
    }

    private boolean isPullRequestExists(String project, String pullRequestKey){
        return fetchPullRequests(project)
                .getPullRequests()
                .stream()
                .anyMatch(pullRequest -> pullRequestKey.equals(pullRequest.getKey()));
    }

    private PullRequestListResult fetchPullRequests(String project) {
        return client.target(serverUrl).path("api").path("project_pull_requests").path("list")
                .queryParam("project", project).request(MediaType.APPLICATION_JSON_TYPE).get(PullRequestListResult.class);
    }

    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public void close() {
        client.close();
    }
}
