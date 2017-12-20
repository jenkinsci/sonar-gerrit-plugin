package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 29.11.2017 14:57
 * $Id$
 */
public class InspectionReport {
    private List<IssueAdapter> issuesList;
    private List<SonarConnector.ReportInfo> reportInfos;

    public InspectionReport(List<SonarConnector.ReportInfo> issueInfos) {
        this.reportInfos = issueInfos;
        // multimap file-to-issues generation for each report
        issuesList = new ArrayList<>();
        for (SonarConnector.ReportInfo info : issueInfos) {
            Report report = info.report;
            generateIssueAdapterList(info.config, report, report.getIssues());
        }
    }

    private String getFilepath(IssueAdapter i) {
        return i.getFilepath();
    }

    public List<IssueAdapter> getIssuesList() {
        return new ArrayList<>(issuesList);
    }

    public Multimap<String, IssueAdapter> asMultimap(Iterable<IssueAdapter> issues) {
        final Multimap<String, IssueAdapter> multimap = LinkedListMultimap.create();
        for (IssueAdapter i : issues) {
            multimap.put(getFilepath(i), i);
        }
        return multimap;
    }

    /**
     * Generates issues wrapper consisting corrected filepath
     */
    private void generateIssueAdapterList(SubJobConfig config, Report report, Iterable<Issue> issues) {
        final ComponentPathBuilder pathBuilder = new ComponentPathBuilder(report.getComponents());
        for (Issue issue : issues) {
            issuesList.add(new SonarQubeIssueAdapter(issue, pathBuilder, config));
        }
    }

    @VisibleForTesting
    Report getRawReport(SubJobConfig config) {
        if (config != null && config.getProjectPath() != null && config.getSonarReportPath() != null) {
            for (SonarConnector.ReportInfo reportInfo : reportInfos) {
                SubJobConfig rconfig = reportInfo.config;
                if (config.getProjectPath().equals(rconfig.getProjectPath())
                        && config.getSonarReportPath().equals(rconfig.getSonarReportPath())) {
                    return reportInfo.report;
                }
            }
        }
        return null;
    }

}
