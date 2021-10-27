package org.jenkinsci.plugins.sonargerrit.sonar.dto;

import java.util.List;

/**
 * @author Réda Housni Alaoui
 */
public class ComponentTasks {

	private List<Task> queue;

	public List<Task> getQueue() {
		return queue;
	}

	public void setQueue(List<Task> queue) {
		this.queue = queue;
	}
}
