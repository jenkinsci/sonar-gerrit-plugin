package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.common.collect.Multimap;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import org.junit.Assert;
import org.jenkinsci.plugins.sonargerrit.DummyRevisionApi;
import org.jenkinsci.plugins.sonargerrit.ReportBasedTest;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.filter.IssueFilter;
import org.jenkinsci.plugins.sonargerrit.inspection.InspectionReportAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.integration.IssueAdapterProcessor;
import org.jenkinsci.plugins.sonargerrit.review.GerritRevisionWrapper;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 05.12.2017 18:52
 *
 * $Id$
 */

/*
 * Slightly different approach to test ComponentPathBuilder
 * The more tests, the better :)
 */
public class ChangedFilesTest extends ReportBasedTest {

    public static final String FILENAME_IN_SONAR = "testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java";
    public static final String FILENAME_IN_GERRIT = "testcontext-viewstore/testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java";
    public static final String EXTRA_FILENAME_IN_GERRIT = "test/" + FILENAME_IN_GERRIT;
    public static final String PREFIX = "testcontext-viewstore";

    @Test
    public void testFilterIssuesByChangedFilesSubModuleNoPathCorrection() throws URISyntaxException, IOException, InterruptedException, RestApiException {
        SubJobConfig config = new SubJobConfig(SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
        final InspectionReport r = getReport(config, false);

        GerritRevisionWrapper w = getRevisionAdapter();

        IssueFilter f = new IssueFilter(new IssueFilterConfig(Severity.INFO.toString(), false, false), r.getIssuesList(), w.getFileToChangedLines());
        Iterable<IssueAdapter> filtered = f.filter();

        boolean contains = isFilterResultContainsFile(FILENAME_IN_GERRIT, filtered);
        Assert.assertFalse(contains);
    }

    @Test
    public void testFilterIssuesByChangedFilesSubModuleWithPathCorrection() throws URISyntaxException, IOException, InterruptedException, RestApiException {
        SubJobConfig config = new SubJobConfig(SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
        config.setAutoMatch(true);
        final InspectionReport r = getReport(config, false);

        GerritRevisionWrapper w = getRevisionAdapter();

        performAutoPathCorrection(r, w);

        IssueFilter f = new IssueFilter(new IssueFilterConfig(Severity.INFO.toString(), false, false), r.getIssuesList(), w.getFileToChangedLines());
        Iterable<IssueAdapter> filtered = f.filter();

        boolean contains = isFilterResultContainsFile(FILENAME_IN_GERRIT, filtered);
        Assert.assertTrue(contains);
    }

    @Test
    public void testFilterIssuesByChangedFilesSubModuleWithSubConfig() throws URISyntaxException, IOException, InterruptedException, RestApiException {

        SubJobConfig config = new SubJobConfig(PREFIX, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
        InspectionReport r = getReport(config, true);

        GerritRevisionWrapper w = getRevisionAdapter();

        IssueFilter f = new IssueFilter(new IssueFilterConfig(Severity.INFO.toString(), false, false), r.getIssuesList(), w.getFileToChangedLines());
        Iterable<IssueAdapter> filtered = f.filter();

        boolean contains = isFilterResultContainsFile(FILENAME_IN_GERRIT, filtered);
        Assert.assertTrue(contains);
    }

    @Test
    public void testFilterIssuesByChangedFilesSubModuleMultiMatch() throws URISyntaxException, IOException, InterruptedException, RestApiException {

        SubJobConfig config = new SubJobConfig(SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
        config.setAutoMatch(true);

        InspectionReport r = getReport(config, false);

        GerritRevisionWrapper w = getRevisionAdapter(EXTRA_FILENAME_IN_GERRIT);

        IssueFilter f = new IssueFilter(new IssueFilterConfig(Severity.INFO.toString(), false, false), r.getIssuesList(), w.getFileToChangedLines());
        Iterable<IssueAdapter> filtered = f.filter();

        boolean contains = isFilterResultContainsFile(FILENAME_IN_GERRIT, filtered);
        Assert.assertFalse(contains);
    }

    protected void performAutoPathCorrection(final InspectionReport r, GerritRevisionWrapper w) {
        //if (inspectionConfig.isPathCorrectionNeeded()) {
        new IssueAdapterProcessor(null, new InspectionReportAdapter() {
            @Override
            public Collection<IssueAdapter> getIssues() {
                return r.getIssuesList();
            }

            @Override
            public Multimap<String, IssueAdapter> getReportData() {
                return null;
            }
        }, w).process();
        //}
    }

    protected boolean isFilterResultContainsFile(String file, Iterable<IssueAdapter> filtered) {
        boolean contains = false;
        for (IssueAdapter issueAdapter : filtered) {
            if (issueAdapter.getFilepath().equals(file)) {
                contains = true;
            }
        }
        return contains;
    }

    protected GerritRevisionWrapper getRevisionAdapter(String... additionalFiles) throws RestApiException {
        final Map<String, FileInfo> files = new HashMap<String, FileInfo>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.status = 'A';
        fileInfo.linesInserted = 9;
        files.put("/COMMIT_MSG", fileInfo);
        fileInfo = new FileInfo();
        fileInfo.linesInserted = 4;
        files.put(FILENAME_IN_GERRIT, fileInfo);
        for (String f : additionalFiles) {
            files.put(f, fileInfo);
        }

        RevisionApi revInfo = new DummyRevisionApi(null) {

            @Override
            public Map<String, FileInfo> files() throws RestApiException {
                return files;
            }

            @Override
            protected DiffInfo generateDiffInfoByPath(String path) {
                DiffInfo info = new DiffInfo();
                info.content = new ArrayList<>();
                info.content.add(createContentEntry(true, 9));
                return info;
            }
        };
        GerritRevisionWrapper gerritRevisionWrapper = new GerritRevisionWrapper(revInfo);
        gerritRevisionWrapper.loadData();
        return gerritRevisionWrapper;
    }

    protected InspectionReport getReport(SubJobConfig config, boolean manuallyCorrected) throws IOException, InterruptedException, URISyntaxException {
        Report report = readreport("report4_maven_multimodule.json");
        Assert.assertEquals(12, report.getComponents().size());
        SonarConnector.ReportInfo info = new SonarConnector.ReportInfo(config, report);
        InspectionReport inspectionReport = new InspectionReport(Arrays.asList(info));
        if (manuallyCorrected) {
            Assert.assertFalse(isFilterResultContainsFile(FILENAME_IN_SONAR, inspectionReport.getIssuesList()));
            Assert.assertTrue(isFilterResultContainsFile(FILENAME_IN_GERRIT, inspectionReport.getIssuesList()));
        } else {
            Assert.assertTrue(isFilterResultContainsFile(FILENAME_IN_SONAR, inspectionReport.getIssuesList()));
            Assert.assertFalse(isFilterResultContainsFile(FILENAME_IN_GERRIT, inspectionReport.getIssuesList()));
        }
        return inspectionReport;
    }
}
