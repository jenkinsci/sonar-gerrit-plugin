package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.config.InspectionConfig;
import org.jenkinsci.plugins.sonargerrit.config.SonarInstallationReader;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.InspectionReportAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.sonar.SonarClient;
import org.jenkinsci.plugins.sonargerrit.sonar.SonarUtil;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Multimap;

import hudson.AbortException;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 14:42
 * <p>
 * $Id$
 */
public class SonarConnector implements InspectionReportAdapter {

    private static final Logger LOGGER = Logger.getLogger(SonarConnector.class.getName());

    private final Run<?, ?> run;
    private final TaskListener listener;
    private final InspectionConfig inspectionConfig;

    private InspectionReport inspectionReport;

    public SonarConnector(Run<?, ?> run, TaskListener listener, InspectionConfig inspectionConfig) {
        this.run = run;
        this.inspectionConfig = inspectionConfig;
        this.listener = listener;
    }

    public void readSonarReports(FilePath workspace) throws IOException, InterruptedException {
        List<ReportInfo> reports = new ArrayList<>();

        if (inspectionConfig.getAnalysisType() == InspectionConfig.DescriptorImpl.AnalysisType.PULL_REQUEST) {
            SonarInstallation sonarInstallation = SonarInstallationReader.getSonarInstallation(inspectionConfig.getSonarInstallationName());
            StringCredentials credentials = sonarInstallation.getCredentials(run);

            SonarClient sonarClient = new SonarClient(sonarInstallation, credentials, listener);
            try {
                int secondsToWait = 5;
                TaskListenerLogger.logMessage(listener, LOGGER, Level.FINE, "jenkins.plugin.sonar.issues.wait", secondsToWait);
                Thread.sleep(secondsToWait * 1000);

                Report report = sonarClient.fetchIssues(
                        SonarUtil.isolateComponentKey(inspectionConfig.getComponent()),
                        TokenMacro.expandAll(run, workspace, listener, inspectionConfig.getPullRequestKey()));
                reports.add(new ReportInfo(new SubJobConfig(), report));
            } catch (MacroEvaluationException e) {
                throw new AbortException(e.getMessage());
            }
        } else {
            for (SubJobConfig subJobConfig : inspectionConfig.getAllSubJobConfigs()) {
                Report report = readSonarReport(workspace, subJobConfig.getSonarReportPath());

                if (report == null) {  //todo fail all? skip errors?
                    TaskListenerLogger
                            .logMessage(listener, LOGGER, Level.SEVERE, "jenkins.plugin.error.path.no.project.config.available");
                    throw new AbortException(getLocalized("jenkins.plugin.error.path.no.project.config.available"));
                }
                reports.add(new ReportInfo(subJobConfig, report));
            }
        }

        inspectionReport = new InspectionReport(reports);
    }

    public Multimap<String, IssueAdapter> getReportData() {
        return inspectionReport.asMultimap(getIssues());
    }

    public Multimap<String, IssueAdapter> getReportData(Iterable<IssueAdapter> issues) {
        return inspectionReport.asMultimap(issues);
    }

    public List<IssueAdapter> getIssues() {
        return inspectionReport.getIssuesList();
    }

    Report getRawReport(SubJobConfig config) {
        return inspectionReport.getRawReport(config);
    }

    private Report readSonarReport(FilePath workspace, String sonarReportPath) throws IOException,
            InterruptedException {
        FilePath reportPath = workspace.child(sonarReportPath);

        // check if report exists
        if (!reportPath.exists()) {
            TaskListenerLogger
                    .logMessage(listener, LOGGER, Level.SEVERE, "jenkins.plugin.error.sonar.report.not.exists", reportPath);
            return null;
        }
        // check if report is a file
        if (reportPath.isDirectory()) {
            TaskListenerLogger
                    .logMessage(listener, LOGGER, Level.SEVERE, "jenkins.plugin.error.sonar.report.path.directory", reportPath);
            return null;
        }

        TaskListenerLogger.logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.inspection.report.loading", reportPath);

        SonarReportBuilder builder = new SonarReportBuilder();
        String reportJson = reportPath.readToString();
        Report report = builder.fromJson(reportJson);

        TaskListenerLogger
                .logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.inspection.report.loaded", report.getIssues().size());
        return report;
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
