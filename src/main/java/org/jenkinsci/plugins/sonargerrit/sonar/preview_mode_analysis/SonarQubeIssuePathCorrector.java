package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import hudson.model.TaskListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.jenkinsci.plugins.sonargerrit.sonar.ByFilenameEndPredicate;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;

/** @author Réda Housni Alaoui */
class SonarQubeIssuePathCorrector implements SonarQubeIssueDecorator {

  private static final Logger LOGGER =
      Logger.getLogger(SonarQubeIssuePathCorrector.class.getName());

  private final TaskListener listener;
  private final Revision revision;

  private final Map<String, String> inspection2revisionFilepaths;

  public SonarQubeIssuePathCorrector(TaskListener listener, Revision revision) {
    this.listener = listener;
    this.revision = revision;
    inspection2revisionFilepaths = new HashMap<>();
  }

  @Override
  public SimpleIssue decorate(SimpleIssue issue) {
    String reviewSystemFilePath = findReviewSystemFilepath(issue, revision.getChangedFiles());
    if (reviewSystemFilePath != null) {
      issue.setFilepath(reviewSystemFilePath);
    }
    return issue;
  }

  private String findReviewSystemFilepath(Issue i, Set<String> files) {
    String filepath = i.getFilepath();
    if (inspection2revisionFilepaths.containsKey(filepath)) {
      return inspection2revisionFilepaths.get(filepath);
    }
    if (files.contains(filepath)) {
      inspection2revisionFilepaths.put(filepath, filepath);
      // return findReviewSystemFilepath(i, files); // extra if and extra get operations
      return filepath;
    }
    String found = null;
    for (String s : files) {
      if (namesMatch(i, s)) {
        if (found == null) {
          found = s;
        } else {
          // achtung! more than one match found!!
          TaskListenerLogger.logMessage(
              listener,
              LOGGER,
              Level.SEVERE,
              "jenkins.plugin.error.more.than.one.file.matched",
              i.getFilepath());
          return null;
        }
      }
    }
    return found;
  }

  private boolean namesMatch(Issue issue, String reviewFilepath) {
    return ByFilenameEndPredicate.apply(reviewFilepath).apply(issue);
  }
}
