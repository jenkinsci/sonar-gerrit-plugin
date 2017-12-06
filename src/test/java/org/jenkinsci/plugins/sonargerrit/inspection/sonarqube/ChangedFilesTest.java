package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import junit.framework.Assert;
import org.jenkinsci.plugins.sonargerrit.DummyRevisionApi;
import org.jenkinsci.plugins.sonargerrit.ReportBasedTest;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.filter.IssueFilter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.review.GerritRevisionWrapper;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 05.12.2017 18:52
 * <p/>
 * $Id$
 */
public class ChangedFilesTest extends ReportBasedTest {
    @Test
    public void testfilterIssuesByChangedFiles() throws URISyntaxException, IOException, InterruptedException, RestApiException {

        Report report = readreport("report4_maven_multimodule.json");
        Assert.assertEquals(12, report.getComponents().size());
        SonarConnector.ReportInfo info = new SonarConnector.ReportInfo(new SubJobConfig(SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH), report);
        InspectionReport r = new InspectionReport(Arrays.asList(info));
//        Assert.assertEquals(3, r.asMultimap().size());

        final Map<String, FileInfo> files = new HashMap<String, FileInfo>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.status = 'A';
        fileInfo.linesInserted = 9;
        files.put("/COMMIT_MSG", fileInfo);
        fileInfo = new FileInfo();
        fileInfo.linesInserted = 4;
        files.put("testcontext-viewstore/testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java", fileInfo);


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
        GerritRevisionWrapper w = new GerritRevisionWrapper(revInfo);

        IssueFilter f = new IssueFilter(new IssueFilterConfig(Severity.INFO.toString(), false, false), r.getIssuesList(), w.getFileToChangedLines());

        Iterable<IssueAdapter> filtered = f.filter();
        boolean contains = false;
        for (IssueAdapter issueAdapter : filtered) {
            if (issueAdapter.getFilepath().equals("testcontext-viewstore/testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java")) {
                contains = true;
            }
        }
//        ByFilenamesPredicate.apply(new HashSet<String>(w.getChangedFiles()));
        // Shows full path to file rather than restricted path visible in report
        Assert.assertTrue(contains);
    }

    @Test
    public void testfilterIssuesByChangedFilesCorrectPath() throws URISyntaxException, IOException, InterruptedException, RestApiException {

        Report report = readreport("report4_maven_multimodule.json");
        Assert.assertEquals(12, report.getComponents().size());
        SonarConnector.ReportInfo info = new SonarConnector.ReportInfo(new SubJobConfig("testcontext-viewstore", SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH), report);
        InspectionReport r = new InspectionReport(Arrays.asList(info));
//        Assert.assertEquals(3, r.asMultimap().size());

        final Map<String, FileInfo> files = new HashMap<String, FileInfo>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.status = 'A';
        fileInfo.linesInserted = 9;
        files.put("/COMMIT_MSG", fileInfo);
        fileInfo = new FileInfo();
        fileInfo.linesInserted = 4;
        files.put("testcontext-viewstore/testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java", fileInfo);


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
        GerritRevisionWrapper w = new GerritRevisionWrapper(revInfo);

        IssueFilter f = new IssueFilter(new IssueFilterConfig(Severity.INFO.toString(), false, false), r.getIssuesList(), w.getFileToChangedLines());

        Iterable<IssueAdapter> filtered = f.filter();
        boolean contains = false;
        for (IssueAdapter issueAdapter : filtered) {
            if (issueAdapter.getFilepath().equals("testcontext-viewstore/testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java")) {
                contains = true;
            }
        }
//        ByFilenamesPredicate.apply(new HashSet<String>(w.getChangedFiles()));
        // Shows full path to file rather than restricted path visible in report
        Assert.assertTrue(contains);
    }
}
