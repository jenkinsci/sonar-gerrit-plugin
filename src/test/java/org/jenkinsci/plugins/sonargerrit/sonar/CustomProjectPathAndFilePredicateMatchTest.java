package org.jenkinsci.plugins.sonargerrit.sonar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RevisionApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.FileInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.jenkinsci.plugins.sonargerrit.gerrit.DummyRevisionApi;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritRevision;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.Assertions;

@EnableJenkinsRule
public abstract class CustomProjectPathAndFilePredicateMatchTest {

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
      SubJobConfig config, boolean expectedResult, String... additionalFilenames)
      throws URISyntaxException, IOException, InterruptedException, RestApiException {

    GerritRevision revision = getRevisionAdapter(additionalFilenames);
    SonarConnector.ReportRecorder reportRecorder = getReport(config, revision);

    IssueFilter f =
        new IssueFilter(
            createFilterConfig(), reportRecorder.getIssuesList(), revision.getFileToChangedLines());
    Iterable<Issue> filtered = f.filter();

    boolean contains = isFilterResultContainsFile(getGerritFilename(), filtered);
    Assertions.assertEquals(expectedResult, contains);
  }

  protected boolean isFilterResultContainsFile(String file, Iterable<Issue> filtered) {
    for (Issue issue : filtered) {
      if (issue.getFilepath().equals(file)) {
        return true;
      }
    }
    return false;
  }

  protected GerritRevision getRevisionAdapter(String... additionalFiles) throws RestApiException {
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
    return GerritRevision.load(revInfo);
  }

  protected SonarConnector.ReportRecorder getReport(SubJobConfig config, GerritRevision revision)
      throws IOException, InterruptedException, URISyntaxException {
    ReportRepresentation report = JsonReports.readReport(getReportFilename());
    Assertions.assertEquals(getCompCount(), report.getComponents().size());
    SonarConnector.ReportInfo info = new SonarConnector.ReportInfo(config, report);
    SonarConnector.ReportRecorder reportRecorder = new ReportRecorderMock();
    SonarConnector sonarConnector = new SonarConnector(reportRecorder);

    InspectionConfig inspectionConfig = new InspectionConfig();
    inspectionConfig.setAutoMatch(config.isAutoMatch());
    sonarConnector.readSonarReports(
        null, inspectionConfig, revision, Collections.singletonList(info));
    return reportRecorder;
  }

  protected int getChangedLine() {
    return 41; // lines with issues in the file are 41 and 129
  }
}
