package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
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
    private List<SonarQubeIssue> issuesList;
    private List<SonarConnector.ReportInfo> reportInfos;

    public InspectionReport(List<SonarConnector.ReportInfo> issueInfos) {
        this.reportInfos = issueInfos;
        // multimap file-to-issues generation for each report
        issuesList = new ArrayList<>();
        for (SonarConnector.ReportInfo info : issueInfos) {
            Report report = info.report;
            generateFixedFilenameIssuesList(info.config.getProjectPath(), report, report.getIssues());
        }
    }


//    private String getFilepath(Issue issue) {
////        final ComponentPathBuilder pathBuilder = new ComponentPathBuilder(report.getComponents());
////        String issueComponent = issue.getComponent();
////        return pathBuilder
////                .buildPrefixedPathForComponentWithKey(issueComponent, projectPath)
////                .or(issueComponent);
//        throw new UnsupportedOperationException();
////        return null;
//    }

    private String getFilepath(SonarQubeIssue i) {
        return i.getFilepath();
    }

    public List<SonarQubeIssue> getIssuesList() {
        return new ArrayList<>(issuesList);
    }

//    public<I extends Issue> Multimap<String, Issue> asMultimap(Iterable<I> issues) {
//        final Multimap<String, Issue> multimap = LinkedListMultimap.create();
//        for (I i : issues) {
//            multimap.put(getFilepath(i), i);
//        }
//        return multimap;
//    }

    public Multimap<String, Issue> asMultimap(Iterable<SonarQubeIssue> issues) {
        final Multimap<String, Issue> multimap = LinkedListMultimap.create();
        for (SonarQubeIssue i : issues) {
            multimap.put(getFilepath(i), i);
        }
        return multimap;
    }

    /**
     * Generates issues wrapper consisting corrected filepath
     */
    private void generateFixedFilenameIssuesList(String projectPath, Report report, Iterable<Issue> issues) {
        final ComponentPathBuilder pathBuilder = new ComponentPathBuilder(report.getComponents());
        for (Issue issue : issues) {
            String issueComponent = issue.getComponent();
            String realFileName = pathBuilder
                    .buildPrefixedPathForComponentWithKey(issueComponent, projectPath)
                    .or(issueComponent);

            issuesList.add(new SonarQubeIssue(issue, realFileName));
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
