package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import static java.util.Objects.requireNonNull;
import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionReport;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 14:42
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
class SonarConnector {

  private static final Logger LOGGER = Logger.getLogger(SonarConnector.class.getName());

  private final TaskListener listener;
  private final PreviewModeAnalysisStrategy strategy;
  private final Revision revision;
  private final SonarInstallation sonarQubeInstallation;

  public SonarConnector(
      TaskListener listener,
      PreviewModeAnalysisStrategy strategy,
      Revision revision,
      SonarInstallation sonarQubeInstallation) {
    this.listener = listener;
    this.strategy = requireNonNull(strategy);
    this.revision = revision;
    this.sonarQubeInstallation = sonarQubeInstallation;
  }

  public InspectionReport readSonarReports(FilePath workspace)
      throws IOException, InterruptedException {
    return readSonarReports(workspace, new SimpleReportRecorder());
  }

  public InspectionReport readSonarReports(FilePath workspace, ReportRecorder reportRecorder)
      throws IOException, InterruptedException {
    List<ReportInfo> reports = new ArrayList<>();
    for (SubJobConfig subJobConfig : strategy.getAllSubJobConfigs()) {
      ReportRepresentation subReport =
          readSonarReport(listener, workspace, subJobConfig.getSonarReportPath());
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

    readSonarReports(reports, reportRecorder);

    return new InspectionReport(reportRecorder.getIssuesList());
  }

  public void readSonarReports(List<ReportInfo> reports, ReportRecorder reportRecorder) {
    reportRecorder.reset();
    reportRecorder.recordReportInfos(reports);

    SonarQubeIssueDecorator decorator;
    if (strategy.isPathCorrectionNeeded()) {
      decorator = new SonarQubeIssuePathCorrector(listener, revision);
    } else {
      decorator = SonarQubeIssueDecorator.Noop.INSTANCE;
    }

    String sonarQubeUrl =
        Optional.ofNullable(sonarQubeInstallation)
            .map(SonarInstallation::getServerUrl)
            .orElse(null);

    // multimap file-to-issues generation for each report
    for (ReportInfo info : reports) {
      ReportRepresentation report = info.report;
      final ComponentPathBuilder pathBuilder = new ComponentPathBuilder(report.getComponents());
      for (IssueRepresentation issue : report.getIssues()) {
        Issue issueToRecord =
            decorator.decorate(new SimpleIssue(issue, pathBuilder, info.config, sonarQubeUrl));
        reportRecorder.recordIssue(issueToRecord);
      }
    }
  }

  private ReportRepresentation readSonarReport(
      TaskListener listener, FilePath workspace, String sonarReportPath)
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
    ReportRepresentation subReport = builder.fromJson(reportJson);

    TaskListenerLogger.logMessage(
        listener,
        LOGGER,
        Level.INFO,
        "jenkins.plugin.inspection.report.loaded",
        subReport.getIssues().size());
    return subReport;
  }

  private static class SimpleReportRecorder implements ReportRecorder {
    private final List<Issue> issuesList = new ArrayList<>();

    @Override
    public void reset() {
      issuesList.clear();
    }

    @Override
    public void recordReportInfos(List<ReportInfo> reportInfos) {
      // Do nothing
    }

    @Override
    public void recordIssue(Issue issue) {
      this.issuesList.add(issue);
    }

    @Override
    public List<Issue> getIssuesList() {
      return new ArrayList<>(issuesList);
    }
  }
}
