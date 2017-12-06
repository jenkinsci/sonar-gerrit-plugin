package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.common.collect.Multimap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.TaskListenerLogger;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 14:42
 * <p>
 * $Id$
 */
public class SonarConnector {

    private static final Logger LOGGER = Logger.getLogger(SonarConnector.class.getName());

    private List<SubJobConfig> subJobConfigs;
    private TaskListener listener;
    private InspectionReport report;

    public SonarConnector(TaskListener listener, List<SubJobConfig> subJobConfigs) {
        this.subJobConfigs = subJobConfigs;
        this.listener = listener;
    }

    public void readSonarReports(FilePath workspace) throws IOException,
            InterruptedException {
        List<ReportInfo> reports = new ArrayList<ReportInfo>();
        for (SubJobConfig subJobConfig : getSubJobConfigs(false)) { // to be replaced by this.subJobConfigs in further releases - this code is to support older versions
            Report report = readSonarReport(workspace, subJobConfig.getSonarReportPath());
            if (report == null) {  //todo fail all? skip errors?
                TaskListenerLogger.logMessage(listener, LOGGER, Level.SEVERE, "jenkins.plugin.error.path.no.project.config.available");
                throw new AbortException("jenkins.plugin.error.path.no.project.config.available");
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

    @SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD")
    private List<SubJobConfig> getSubJobConfigs() {
        return getSubJobConfigs(true);
    }

    private List<SubJobConfig> getSubJobConfigs(boolean addDefault) {
        if (subJobConfigs == null) {
            subJobConfigs = new ArrayList<SubJobConfig>();
            // add configuration from previous plugin version
            if (addDefault) {
                subJobConfigs.add(new SubJobConfig(SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH,
                        SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH));
            }
        }
        return subJobConfigs;
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
