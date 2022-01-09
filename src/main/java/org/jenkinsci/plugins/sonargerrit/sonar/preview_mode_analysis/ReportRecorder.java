package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import java.util.List;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;

/** @author Réda Housni Alaoui */
interface ReportRecorder {

  void reset();

  void recordReportInfos(List<ReportInfo> reportInfos);

  void recordIssue(Issue issue);

  List<Issue> getIssuesList();
}
