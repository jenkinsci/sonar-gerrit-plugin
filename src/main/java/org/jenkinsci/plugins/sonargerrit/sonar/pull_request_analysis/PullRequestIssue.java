package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import me.redaalaoui.org.sonarqube.ws.Issues;
import me.redaalaoui.org.sonarqube.ws.ProjectPullRequests;
import org.jenkinsci.plugins.sonargerrit.sonar.Components;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.Rule;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.jenkinsci.plugins.sonargerrit.util.UrlBuilder;

/** @author Réda Housni Alaoui */
class PullRequestIssue implements Issue {

  private final ProjectPullRequests.PullRequest pullRequest;
  private final Issues.Issue issue;
  private final String filePath;
  private final String sonarQubeUrl;

  public PullRequestIssue(
      ProjectPullRequests.PullRequest pullRequest,
      Components components,
      Issues.Issue issue,
      String sonarQubeUrl) {
    this.pullRequest = requireNonNull(pullRequest);
    this.issue = requireNonNull(issue);
    this.filePath =
        components
            .buildPrefixedPathForComponentWithKey(issue.getComponent(), "")
            .or(issue.getComponent());
    this.sonarQubeUrl = requireNonNull(sonarQubeUrl);
  }

  @Override
  public String inspectorName() {
    return "Sonar";
  }

  @Override
  public String inspectionId() {
    return pullRequest.getKey();
  }

  @Override
  public Optional<String> detailUrl() {
    return Optional.of(
        new UrlBuilder()
            .addSegment(sonarQubeUrl)
            .addSegment("project")
            .addSegment("issues")
            .addQueryParameter("id", issue.getProject())
            .addQueryParameter("open", issue.getKey())
            .addQueryParameter("pullRequest", pullRequest.getKey())
            .build());
  }

  @Override
  public String getFilepath() {
    return filePath;
  }

  @Override
  public String getKey() {
    return issue.getKey();
  }

  @Override
  public String getComponent() {
    return issue.getComponent();
  }

  @Override
  public Integer getLine() {
    return issue.getLine();
  }

  @Override
  public String getMessage() {
    return issue.getMessage();
  }

  @Override
  public Severity getSeverity() {
    return Severity.valueOf(issue.getSeverity().name());
  }

  @Override
  public String getRule() {
    return issue.getRule();
  }

  @Override
  public String getRuleUrl() {
    return new Rule(issue.getRule()).createUrl(sonarQubeUrl);
  }

  @Override
  public String getStatus() {
    return issue.getStatus();
  }

  @Override
  public boolean isNew() {
    return true;
  }

  @Override
  public Date getCreationDate() {
    return Date.from(ZonedDateTime.parse(issue.getCreationDate()).toInstant());
  }
}
