package org.jenkinsci.plugins.sonargerrit.sonar.dto;

import java.util.List;

/**
 * @author Réda Housni Alaoui
 */
public class PullRequestListResult {

	private List<PullRequest> pullRequests;

	public List<PullRequest> getPullRequests() {
		return pullRequests;
	}

	public void setPullRequests(List<PullRequest> pullRequests) {
		this.pullRequests = pullRequests;
	}
}
