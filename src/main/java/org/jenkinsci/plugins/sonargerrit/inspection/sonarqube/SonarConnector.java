package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.config.InspectionConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.InspectionReportAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 14:42
 *
 * <p>$Id$
 */
public class SonarConnector implements InspectionReportAdapter {

  private static final Logger LOGGER = Logger.getLogger(SonarConnector.class.getName());

  private final TaskListener listener;
  private InspectionReport report;
  private final InspectionConfig inspectionConfig;

  public SonarConnector(TaskListener listener, InspectionConfig inspectionConfig) {
    this.inspectionConfig = inspectionConfig;
    this.listener = listener;
  }

  public void readSonarReports(FilePath workspace) throws IOException, InterruptedException {
    List<ReportInfo> reports = new ArrayList<>();
    for (SubJobConfig subJobConfig : inspectionConfig.getAllSubJobConfigs()) {
      Report subReport = readSonarReport(workspace, subJobConfig.getSonarReportPath());
      if (subReport == null) {
        TaskListenerLogger.logMessage(
            listener,
            LOGGER,
            Level.SEVERE,
            "jenkins.plugin.error.path.no.project.config.available");
        throw new AbortException(
            getLocalized("jenkins.plugin.error.path.no.project.config.available"));
      }
      reports.add(new ReportInfo(subJobConfig, subReport));
    }
    report = new InspectionReport(reports);
  }

  @Override
  public List<IssueAdapter> getIssues() {
    return report.getIssuesList();
  }

  Report getRawReport(SubJobConfig config) {
    return report.getRawReport(config);
  }

  private Report readSonarReport(FilePath workspace, String sonarReportPath)
      throws IOException, InterruptedException {
    FilePath reportPath = workspace.child(sonarReportPath);

    // check if report exists
    if (!reportPath.exists()) {
      TaskListenerLogger.logMessage(
          listener,
          LOGGER,
          Level.SEVERE,
          "jenkins.plugin.error.sonar.report.not.exists",
          reportPath);
      return null;
    }
    // check if report is a file
    if (reportPath.isDirectory()) {
      TaskListenerLogger.logMessage(
          listener,
          LOGGER,
          Level.SEVERE,
          "jenkins.plugin.error.sonar.report.path.directory",
          reportPath);
      return null;
    }

    TaskListenerLogger.logMessage(
        listener, LOGGER, Level.INFO, "jenkins.plugin.inspection.report.loading", reportPath);

    SonarReportBuilder builder = new SonarReportBuilder();
    String reportJson = reportPath.readToString();
    Report subReport = builder.fromJson(reportJson);

    TaskListenerLogger.logMessage(
        listener,
        LOGGER,
        Level.INFO,
        "jenkins.plugin.inspection.report.loaded",
        subReport.getIssues().size());
    return subReport;
  }

  static class ReportInfo {

    public final SubJobConfig config;
    public final Report report;

    public ReportInfo(SubJobConfig config, Report report) {
      this.config = config;
      this.report = report;
    }
  }
}
