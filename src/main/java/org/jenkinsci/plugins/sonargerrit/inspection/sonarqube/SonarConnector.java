package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.common.collect.Multimap;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.config.InspectionConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.InspectionReportAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.util.Localization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 14:42
 * <p>
 * $Id$
 */
public class SonarConnector implements InspectionReportAdapter {

    private static final Logger LOGGER = Logger.getLogger(SonarConnector.class.getName());

    private TaskListener listener;
    private InspectionReport report;
    private InspectionConfig inspectionConfig;

    public SonarConnector(TaskListener listener, InspectionConfig inspectionConfig) {
        this.inspectionConfig = inspectionConfig;
        this.listener = listener;
    }

    public void readSonarReports(FilePath workspace) throws IOException,
            InterruptedException {
        List<ReportInfo> reports = new ArrayList<ReportInfo>();
        for (SubJobConfig subJobConfig : inspectionConfig.getAllSubJobConfigs()) {
            Report report = readSonarReport(workspace, subJobConfig.getSonarReportPath());
            if (report == null) {  //todo fail all? skip errors?
                TaskListenerLogger.logMessage(listener, LOGGER, Level.SEVERE, "jenkins.plugin.error.path.no.project.config.available");
                throw new AbortException(getLocalized("jenkins.plugin.error.path.no.project.config.available"));
            }
            reports.add(new ReportInfo(subJobConfig, report));
        }
        report = new InspectionReport(reports);
    }

    public Multimap<String, IssueAdapter> getReportData() {
        return report.asMultimap(getIssues());
    }

    public Multimap<String, IssueAdapter> getReportData(Iterable<IssueAdapter> issues) {
        return report.asMultimap(issues);
    }

    public List<IssueAdapter> getIssues() {
        return report.getIssuesList();
    }

    Report getRawReport(SubJobConfig config) {
        return report.getRawReport(config);
    }

    private Report readSonarReport(FilePath workspace, String sonarReportPath) throws IOException,
            InterruptedException {
        FilePath reportPath = workspace.child(sonarReportPath);

        // check if report exists
        if (!reportPath.exists()) {
            TaskListenerLogger.logMessage(listener, LOGGER, Level.SEVERE, "jenkins.plugin.error.sonar.report.not.exists", reportPath);
            return null;
        }
        // check if report is a file
        if (reportPath.isDirectory()) {
            TaskListenerLogger.logMessage(listener, LOGGER, Level.SEVERE, "jenkins.plugin.error.sonar.report.path.directory", reportPath);
            return null;
        }

        TaskListenerLogger.logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.inspection.report.loading", reportPath);

        SonarReportBuilder builder = new SonarReportBuilder();
        String reportJson = reportPath.readToString();
        Report report = builder.fromJson(reportJson);

        TaskListenerLogger.logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.inspection.report.loaded", report.getIssues().size());
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
