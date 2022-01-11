package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.Date;
import org.jenkinsci.plugins.sonargerrit.sonar.Components;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.Rule;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.sonarqube.ws.Issues;

/** @author Réda Housni Alaoui */
class PullRequestIssue implements Issue {

  private final Issues.Issue issue;
  private final String filePath;
  private final String sonarQubeUrl;

  public PullRequestIssue(Components components, Issues.Issue issue, String sonarQubeUrl) {
    this.issue = requireNonNull(issue);
    this.filePath =
        components
            .buildPrefixedPathForComponentWithKey(issue.getComponent(), "")
            .or(issue.getComponent());
    this.sonarQubeUrl = requireNonNull(sonarQubeUrl);
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
  public String getRuleLink() {
    return new Rule(issue.getRule()).createLink(sonarQubeUrl);
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
