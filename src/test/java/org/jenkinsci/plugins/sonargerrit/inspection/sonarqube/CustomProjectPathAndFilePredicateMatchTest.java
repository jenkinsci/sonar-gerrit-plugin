package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.common.collect.Multimap;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import junit.framework.Assert;
import org.jenkinsci.plugins.sonargerrit.DummyRevisionApi;
import org.jenkinsci.plugins.sonargerrit.ReportBasedTest;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.filter.IssueFilter;
import org.jenkinsci.plugins.sonargerrit.inspection.InspectionReportAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.integration.IssueAdapterProcessor;
import org.jenkinsci.plugins.sonargerrit.review.GerritRevisionWrapper;

public abstract class CustomProjectPathAndFilePredicateMatchTest extends ReportBasedTest {

  public static final String FILENAME_IN_SONAR =
      "testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java";
  public static final String FILENAME_IN_GERRIT =
      "testcontext-viewstore/testcontext-viewstore-persistence/src/main/java/com/example/testcontext/persistence/entity/User.java";
  public static final String PREFIX = "testcontext-viewstore";
  public static final String REPORT_PATH = "report4_maven_multimodule.json";

  protected String getGerritFilename() {
    return FILENAME_IN_GERRIT;
  }

  protected String getSonarFilename() {
    return FILENAME_IN_SONAR;
  }

  protected String getReportFilename() {
    return REPORT_PATH;
  }

  protected int getCompCount() {
    return 12;
  }

  protected abstract IssueFilterConfig createFilterConfig();

  protected void performTest(
      SubJobConfig config,
      boolean manuallyCorrected,
      boolean expectedResult,
      String... additionalFilenames)
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    InspectionReport r = getReport(config, manuallyCorrected);

    GerritRevisionWrapper w = getRevisionAdapter(additionalFilenames);

    if (config.isAutoMatch()) {
      performAutoPathCorrection(r, w);
    }

    IssueFilter f =
        new IssueFilter(createFilterConfig(), r.getIssuesList(), w.getFileToChangedLines());
    Iterable<IssueAdapter> filtered = f.filter();

    boolean contains = isFilterResultContainsFile(getGerritFilename(), filtered);
    Assert.assertEquals(expectedResult, contains);
  }

  protected void performAutoPathCorrection(final InspectionReport r, GerritRevisionWrapper w) {
    // if (inspectionConfig.isPathCorrectionNeeded()) {
    new IssueAdapterProcessor(
            null,
            new InspectionReportAdapter() {
              @Override
              public Collection<IssueAdapter> getIssues() {
                return r.getIssuesList();
              }

              @Override
              public Multimap<String, IssueAdapter> getReportData() {
                return null;
              }
            },
            w)
        .process();
    // }
  }

  protected boolean isFilterResultContainsFile(String file, Iterable<IssueAdapter> filtered) {
    for (IssueAdapter issueAdapter : filtered) {
      if (issueAdapter.getFilepath().equals(file)) {
        return true;
      }
    }
    return false;
  }

  protected GerritRevisionWrapper getRevisionAdapter(String... additionalFiles)
      throws RestApiException {
    final Map<String, FileInfo> files = new HashMap<>();
    FileInfo fileInfo = new FileInfo();
    fileInfo.status = 'A';
    fileInfo.linesInserted = 1;
    fileInfo.linesDeleted = 1;
    files.put("/COMMIT_MSG", fileInfo);

    fileInfo = new FileInfo();
    fileInfo.linesInserted = 1;
    fileInfo.linesDeleted = 1;
    files.put(getGerritFilename(), fileInfo);
    for (String f : additionalFiles) {
      files.put(f, fileInfo);
    }

    RevisionApi revInfo =
        new DummyRevisionApi(null) {

          @Override
          public Map<String, FileInfo> files() {
            return files;
          }

          @Override
          protected DiffInfo generateDiffInfoByPath(String path) {
            DiffInfo info = new DiffInfo();
            info.content = new ArrayList<>();
            info.content.add(createContentEntry(false, getChangedLine() - 1));
            info.content.add(createContentEntry(true, 1));
            info.content.add(createContentEntry(false, 150 - getChangedLine() - 1));

            return info;
          }
        };
    GerritRevisionWrapper gerritRevisionWrapper = new GerritRevisionWrapper(revInfo);
    gerritRevisionWrapper.loadData();
    return gerritRevisionWrapper;
  }

  protected InspectionReport getReport(SubJobConfig config, boolean manuallyCorrected)
      throws IOException, InterruptedException, URISyntaxException {
    Report report = readreport(getReportFilename());
    Assert.assertEquals(getCompCount(), report.getComponents().size());
    SonarConnector.ReportInfo info = new SonarConnector.ReportInfo(config, report);
    InspectionReport inspectionReport = new InspectionReport(Collections.singletonList(info));
    if (manuallyCorrected) {
      Assert.assertFalse(
          isFilterResultContainsFile(getSonarFilename(), inspectionReport.getIssuesList()));
      Assert.assertTrue(
          isFilterResultContainsFile(getGerritFilename(), inspectionReport.getIssuesList()));
    } else {
      Assert.assertTrue(
          isFilterResultContainsFile(getSonarFilename(), inspectionReport.getIssuesList()));
      Assert.assertFalse(
          isFilterResultContainsFile(getGerritFilename(), inspectionReport.getIssuesList()));
    }
    return inspectionReport;
  }

  protected int getChangedLine() {
    return 41; // lines with issues in the file are 41 and 129
  }
}
