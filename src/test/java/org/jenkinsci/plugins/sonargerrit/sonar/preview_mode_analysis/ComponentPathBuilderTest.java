package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import com.google.common.collect.Multimap;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.jenkinsci.plugins.sonargerrit.sonar.Components;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionReport;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 30.11.2017 18:26
 *
 * <p>$Id$
 */
public class ComponentPathBuilderTest {
  @Test
  public void testNestedComponent() throws IOException, InterruptedException {
    String filename = "sc-rep1.json";
    SubJobConfig config = createConfig("", filename);

    ReportRecorderMock recordedReports = new ReportRecorderMock();
    InspectionReport inspectionReport = readSonarReport(recordedReports, config);
    List<Issue> issues = inspectionReport.getIssues();
    Assertions.assertEquals(1, issues.size());
    Issue issue = issues.get(0);

    ReportRepresentation report = recordedReports.getRawReport(config);
    Components builder = new Components(report.getComponents());
    String issueComponent = issue.getComponent();
    String realFileName =
        builder
            .buildPrefixedPathForComponentWithKey(issueComponent, config.getProjectPath())
            .or(issueComponent);
    Assertions.assertEquals(
        "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
        realFileName);

    Assertions.assertEquals(
        "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
        issue.getFilepath()); // logic of another class - todo move out there
  }

  @Test
  public void testSuperNestedComponent() throws IOException, InterruptedException {
    String filename = "report3_with-nested-subprojects.json";
    SubJobConfig config = createConfig("testfolder", filename);
    ReportRecorderMock recordedReports = new ReportRecorderMock();
    InspectionReport inspectionReport = readSonarReport(recordedReports, config);
    List<Issue> issues = inspectionReport.getIssues();
    Assertions.assertEquals(8, issues.size());

    ReportRepresentation report = recordedReports.getRawReport(config);
    Components builder = new Components(report.getComponents());
    testIssue(
        builder,
        issues.get(0),
        config.getProjectPath(),
        "testfolder/base/core/proj1/src/main/java/proj1/Proj1.java");
    testIssue(
        builder,
        issues.get(1),
        config.getProjectPath(),
        "testfolder/base/core/proj1/src/main/java/proj1/Proj1.java");
    testIssue(
        builder,
        issues.get(2),
        config.getProjectPath(),
        "testfolder/base/core/proj1/src/main/java/proj1/Proj1.java");
    testIssue(
        builder,
        issues.get(3),
        config.getProjectPath(),
        "testfolder/base/core/proj2/src/main/java/com/proj2/Proj2.java");
    testIssue(
        builder,
        issues.get(4),
        config.getProjectPath(),
        "testfolder/base/core/proj2/sub2/src/main/java/com/proj2/sub2/SubProj2.java");
    testIssue(
        builder,
        issues.get(5),
        config.getProjectPath(),
        "testfolder/base/core/proj2/sub2/sub22/sub2222/sub22222/src/main/java/com/proj2/sub2/SubProj22222.java");
    testIssue(
        builder,
        issues.get(6),
        config.getProjectPath(),
        "testfolder/base/com.acme.util/src/main/java/com/acme/util/Util.java");
    testIssue(
        builder,
        issues.get(7),
        config.getProjectPath(),
        "testfolder/com.acme.app/src/main/java/com/acme/app/App.java");

    Multimap<String, Issue> multimap = Issue.asMultimap(inspectionReport.getIssues());
    Assertions.assertEquals(8, multimap.size());
    Assertions.assertEquals(
        3, multimap.get("testfolder/base/core/proj1/src/main/java/proj1/Proj1.java").size());
    Assertions.assertEquals(
        1,
        multimap
            .get("testfolder/base/core/proj2/sub2/src/main/java/com/proj2/sub2/SubProj2.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder/base/core/proj2/sub2/sub22/sub2222/sub22222/src/main/java/com/proj2/sub2/SubProj22222.java")
            .size());
    Assertions.assertEquals(
        1, multimap.get("testfolder/base/core/proj2/src/main/java/com/proj2/Proj2.java").size());
    Assertions.assertEquals(
        1,
        multimap.get("testfolder/base/com.acme.util/src/main/java/com/acme/util/Util.java").size());
    Assertions.assertEquals(
        1, multimap.get("testfolder/com.acme.app/src/main/java/com/acme/app/App.java").size());
  }

  @Test
  public void testEmptyProjectPath() throws IOException, InterruptedException {
    String filename = "filter.json";
    SubJobConfig config = createConfig("", filename);

    InspectionReport report = readSonarReport(new ReportRecorderMock(), config);
    List<Issue> issues = report.getIssues();
    Assertions.assertEquals(19, issues.size());

    Multimap<String, Issue> multimap = Issue.asMultimap(report.getIssues());
    Assertions.assertEquals(19, multimap.size());
    Assertions.assertEquals(8, multimap.keySet().size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java")
            .size());
    Assertions.assertEquals(
        2,
        multimap.get("juice-jpa/src/main/java/com/turquoise/juice/jpa/DBInterceptor.java").size());
    Assertions.assertEquals(
        8,
        multimap
            .get(
                "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java")
            .size());
    Assertions.assertEquals(
        4,
        multimap
            .get("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/xml/XmlModule.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "juice-events/src/main/java/com/turquoise/juice/events/ClassgenHandlerInvocator.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get("juice-events/src/main/java/com/turquoise/juice/events/EnumMatcher.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get("juice-events/src/main/java/com/turquoise/juice/events/EventDispatcher.java")
            .size());
    Assertions.assertEquals(
        1, multimap.get("src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());
  }

  @Test
  public void testSimpleProjectPath() throws IOException, InterruptedException {
    String filename = "filter.json";
    SubJobConfig config = createConfig("testfolder", filename);

    InspectionReport report = readSonarReport(new ReportRecorderMock(), config);
    List<Issue> issues = report.getIssues();
    Assertions.assertEquals(19, issues.size());

    Multimap<String, Issue> multimap = Issue.asMultimap(report.getIssues());
    Assertions.assertEquals(19, multimap.size());
    Assertions.assertEquals(8, multimap.keySet().size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java")
            .size());
    Assertions.assertEquals(
        2,
        multimap
            .get("testfolder/juice-jpa/src/main/java/com/turquoise/juice/jpa/DBInterceptor.java")
            .size());
    Assertions.assertEquals(
        8,
        multimap
            .get(
                "testfolder/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java")
            .size());
    Assertions.assertEquals(
        4,
        multimap
            .get(
                "testfolder/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/xml/XmlModule.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder/juice-events/src/main/java/com/turquoise/juice/events/ClassgenHandlerInvocator.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder/juice-events/src/main/java/com/turquoise/juice/events/EnumMatcher.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder/juice-events/src/main/java/com/turquoise/juice/events/EventDispatcher.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get("testfolder/src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java")
            .size());
  }

  @Test
  public void testTwoProjectPaths() throws IOException, InterruptedException {
    SubJobConfig config1 = createConfig("testfolder1", "report1.json");
    SubJobConfig config2 = createConfig("testfolder2", "report2.json");
    InspectionReport report = readSonarReport(new ReportRecorderMock(), config1, config2);
    List<Issue> issues = report.getIssues();
    Assertions.assertEquals(19, issues.size());

    Multimap<String, Issue> multimap = Issue.asMultimap(report.getIssues());
    Assertions.assertEquals(19, multimap.size());
    Assertions.assertEquals(9, multimap.keySet().size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder1/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java")
            .size());
    Assertions.assertEquals(
        2,
        multimap
            .get("testfolder1/juice-jpa/src/main/java/com/turquoise/juice/jpa/DBInterceptor.java")
            .size());
    Assertions.assertEquals(
        5,
        multimap
            .get(
                "testfolder1/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java")
            .size());
    Assertions.assertEquals(
        3,
        multimap
            .get(
                "testfolder2/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java")
            .size());
    Assertions.assertEquals(
        4,
        multimap
            .get(
                "testfolder2/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/xml/XmlModule.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder2/juice-events/src/main/java/com/turquoise/juice/events/ClassgenHandlerInvocator.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder2/juice-events/src/main/java/com/turquoise/juice/events/EnumMatcher.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get(
                "testfolder2/juice-events/src/main/java/com/turquoise/juice/events/EventDispatcher.java")
            .size());
    Assertions.assertEquals(
        1,
        multimap
            .get("testfolder2/src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java")
            .size());
  }

  private void testIssue(
      Components builder, Issue issue, String projectPath, String expectedFilename) {
    String issueComponent = issue.getComponent();
    String realFileName =
        builder
            .buildPrefixedPathForComponentWithKey(issueComponent, projectPath)
            .or(issueComponent);
    Assertions.assertEquals(expectedFilename, realFileName);

    Assertions.assertEquals(expectedFilename, issue.getFilepath());
  }

  protected InspectionReport readSonarReport(ReportRecorder reportRecorder, SubJobConfig... configs)
      throws IOException, InterruptedException {
    return new SonarConnector(null, buildStrategy(configs), null, null)
        .readSonarReports(new FilePath(new File("")), reportRecorder);
  }

  private PreviewModeAnalysisStrategy buildStrategy(SubJobConfig... configs) {
    PreviewModeAnalysisStrategy strategy = new PreviewModeAnalysisStrategy();
    strategy.setType(PreviewModeAnalysisStrategy.DescriptorImpl.MULTI_TYPE);
    strategy.setSubJobConfigs(Arrays.asList(configs));
    return strategy;
  }

  private SubJobConfig createConfig(String ppath, String spath) {
    return new SubJobConfig(ppath, RESOURCES_PATH_PREFIX + spath);
  }

  public static final String RESOURCES_PATH_PREFIX = "src/test/resources/";
}
