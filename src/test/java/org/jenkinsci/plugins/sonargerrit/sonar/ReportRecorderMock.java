package org.jenkinsci.plugins.sonargerrit.sonar;

import java.util.ArrayList;
import java.util.List;

/** @author Réda Housni Alaoui */
public class ReportRecorderMock implements SonarConnector.ReportRecorder {

  private final List<SonarConnector.ReportInfo> reportInfos = new ArrayList<>();
  private final List<Issue> issuesList = new ArrayList<>();

  @Override
  public void reset() {
    reportInfos.clear();
    issuesList.clear();
  }

  @Override
  public void recordReportInfos(List<SonarConnector.ReportInfo> reportInfos) {
    this.reportInfos.addAll(reportInfos);
  }

  @Override
  public void recordIssue(Issue issue) {
    this.issuesList.add(issue);
  }

  @Override
  public List<Issue> getIssuesList() {
    return issuesList;
  }

  public ReportRepresentation getRawReport(SubJobConfig config) {
    if (config == null || config.getProjectPath() == null || config.getSonarReportPath() == null) {
      return null;
    }
    for (SonarConnector.ReportInfo reportInfo : reportInfos) {
      SubJobConfig reportConfig = reportInfo.config;
      if (config.getProjectPath().equals(reportConfig.getProjectPath())
          && config.getSonarReportPath().equals(reportConfig.getSonarReportPath())) {
        return reportInfo.report;
      }
    }
    return null;
  }
}
