package org.jenkinsci.plugins.sonargerrit.sonar;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 14:42
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public class SonarConnector implements InspectionReportAdapter {

  private static final Logger LOGGER = Logger.getLogger(SonarConnector.class.getName());

  private final ReportRecorder reportRecorder;

  public SonarConnector() {
    this(null);
  }

  public SonarConnector(ReportRecorder reportRecorder) {
    this.reportRecorder = Optional.ofNullable(reportRecorder).orElseGet(SimpleReportRecorder::new);
  }

  public SonarConnector readSonarReports(
      TaskListener listener,
      InspectionConfig inspectionConfig,
      Revision revision,
      FilePath workspace)
      throws IOException, InterruptedException {
    List<ReportInfo> reports = new ArrayList<>();
    for (SubJobConfig subJobConfig : inspectionConfig.getAllSubJobConfigs()) {
      Report subReport = readSonarReport(listener, workspace, subJobConfig.getSonarReportPath());
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

    readSonarReports(listener, inspectionConfig, revision, reports);

    return this;
  }

  public void readSonarReports(
      TaskListener listener,
      InspectionConfig inspectionConfig,
      Revision revision,
      List<ReportInfo> reports) {
    reportRecorder.reset();
    reportRecorder.recordReportInfos(reports);

    SonarQubeIssueDecorator decorator;
    if (inspectionConfig.isPathCorrectionNeeded()) {
      decorator = new SonarQubeIssuePathCorrector(listener, revision);
    } else {
      decorator = SonarQubeIssueDecorator.Noop.INSTANCE;
    }

    // multimap file-to-issues generation for each report
    for (ReportInfo info : reports) {
      Report report = info.report;
      final ComponentPathBuilder pathBuilder = new ComponentPathBuilder(report.getComponents());
      for (Issue issue : report.getIssues()) {
        IssueAdapter issueToRecord =
            decorator.decorate(new SonarQubeIssue(issue, pathBuilder, info.config));
        reportRecorder.recordIssue(issueToRecord);
      }
    }
  }

  @Override
  public List<IssueAdapter> getIssues() {
    return reportRecorder.getIssuesList();
  }

  private Report readSonarReport(TaskListener listener, FilePath workspace, String sonarReportPath)
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

  public interface ReportRecorder {

    void reset();

    void recordReportInfos(List<ReportInfo> reportInfos);

    void recordIssue(IssueAdapter issue);

    List<IssueAdapter> getIssuesList();
  }

  private static class SimpleReportRecorder implements ReportRecorder {
    private final List<IssueAdapter> issuesList = new ArrayList<>();

    @Override
    public void reset() {
      issuesList.clear();
    }

    @Override
    public void recordReportInfos(List<ReportInfo> reportInfos) {
      // Do nothing
    }

    @Override
    public void recordIssue(IssueAdapter issue) {
      this.issuesList.add(issue);
    }

    @Override
    public List<IssueAdapter> getIssuesList() {
      return new ArrayList<>(issuesList);
    }
  }

  public static class ReportInfo {

    public final SubJobConfig config;
    public final Report report;

    public ReportInfo(SubJobConfig config, Report report) {
      this.config = config;
      this.report = report;
    }
  }
}
