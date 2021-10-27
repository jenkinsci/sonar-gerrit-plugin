package org.jenkinsci.plugins.sonargerrit.sonar.dto;

/**
 * @author Réda Housni Alaoui
 */
public class Task {

	private String status;
	private String errorMessage;
	private String pullRequest;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getPullRequest() {
		return pullRequest;
	}

	public void setPullRequest(String pullRequest) {
		this.pullRequest = pullRequest;
	}
}
