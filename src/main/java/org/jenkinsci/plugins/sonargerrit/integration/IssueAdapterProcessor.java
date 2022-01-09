package org.jenkinsci.plugins.sonargerrit.integration;

import hudson.model.TaskListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByFilenameEndPredicate;
import org.jenkinsci.plugins.sonargerrit.inspection.InspectionReportAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.review.Revision;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.12.2017 21:16 */
@Restricted(NoExternalUse.class)
public class IssueAdapterProcessor {
  private final InspectionReportAdapter inspectionReport;
  private final Revision revision;
  private final Map<String, String> inspection2revisionFilepaths;
  private final TaskListener listener;

  private static final Logger LOGGER = Logger.getLogger(IssueAdapterProcessor.class.getName());

  public IssueAdapterProcessor(
      TaskListener listener, InspectionReportAdapter inspectionReport, Revision revision) {
    this.listener = listener;
    this.inspectionReport = inspectionReport;
    this.revision = revision;
    this.inspection2revisionFilepaths = new HashMap<>();
  }

  public void process() {
    Iterable<IssueAdapter> issues = inspectionReport.getIssues();
    Set<String> changedFiles = revision.getChangedFiles();
    for (IssueAdapter issue : issues) {
      String reviewSystemFilePath = findReviewSystemFilepath(issue, changedFiles);
      if (reviewSystemFilePath != null) {
        issue.setFilepath(reviewSystemFilePath);
      }
    }
  }

  private String findReviewSystemFilepath(IssueAdapter i, Set<String> files) {
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

  private boolean namesMatch(IssueAdapter issue, String reviewFilepath) {
    return ByFilenameEndPredicate.apply(reviewFilepath).apply(issue);
  }
}
