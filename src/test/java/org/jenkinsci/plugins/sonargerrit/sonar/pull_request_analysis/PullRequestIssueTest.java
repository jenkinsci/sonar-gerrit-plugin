package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import org.jenkinsci.plugins.sonargerrit.sonar.Components;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sonarqube.ws.Issues;
import org.sonarqube.ws.ProjectPullRequests;

/** @author Réda Housni Alaoui */
class PullRequestIssueTest {

  private PullRequestIssue pullRequestIssue;

  @BeforeEach
  void beforeEach() {
    ProjectPullRequests.PullRequest pullRequest =
        ProjectPullRequests.PullRequest.newBuilder().setKey("pr-key").build();
    Components components = mock(Components.class);
    when(components.buildPrefixedPathForComponentWithKey(any(), any()))
        .thenReturn(Optional.absent());
    Issues.Issue issue =
        Issues.Issue.newBuilder().setProject("project-key").setKey("issue-key").build();

    pullRequestIssue =
        new PullRequestIssue(pullRequest, components, issue, "https://sonarqube.example.org");
  }

  @Test
  @DisplayName("Check detailUrl")
  void test1() {
    assertThat(pullRequestIssue.detailUrl())
        .contains(
            "https://sonarqube.example.org/project/issues?id=project-key&pullRequest=pr-key&open=issue-key");
  }

  @Test
  @DisplayName("Check inspectionId")
  void test2() {
    assertThat(pullRequestIssue.inspectionId()).isEqualTo("pr-key");
  }
}
