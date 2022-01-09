package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 30.11.2017 15:10
 *
 * <p>$Id$
 */
public class SonarConnectorTest {
  public static final String RESOURCES_PATH_PREFIX = "src/test/resources/";

  @Test
  public void testReadSingleSimpleReport() throws IOException, InterruptedException {
    String filename = "one_issue.json";
    SubJobConfig config = createConfig("", "one_issue.json");

    ReportRecorderMock recordedReports = new ReportRecorderMock();
    InspectionReport inspectionReport = readSonarReport(recordedReports, config);
    Assertions.assertNotNull(inspectionReport);
    Assertions.assertNotNull(inspectionReport.getIssues());
    Assertions.assertEquals(1, inspectionReport.getIssues().size());
    ReportDataChecker.checkFile(filename, recordedReports.getRawReport(config));
  }

  @Test
  public void testReadTwoSimpleReports() throws IOException, InterruptedException {
    String filename1 = "sc-rep1.json";
    String filename2 = "sc-rep2.json";
    SubJobConfig config1 = createConfig("", filename1);
    SubJobConfig config2 = createConfig("", filename2);

    ReportRecorderMock recordedReports = new ReportRecorderMock();
    InspectionReport inspectionReport = readSonarReport(recordedReports, config1, config2);
    Assertions.assertNotNull(inspectionReport);
    Assertions.assertNotNull(inspectionReport.getIssues());
    Assertions.assertEquals(2, inspectionReport.getIssues().size());
    ReportDataChecker.checkFile(filename1, recordedReports.getRawReport(config1));
    ReportDataChecker.checkFile(filename2, recordedReports.getRawReport(config2));
  }

  @Test
  public void testReadThreeSimpleReports() throws IOException, InterruptedException {
    String filename1 = "sc-rep1.json";
    String filename2 = "sc-rep2.json";
    String filename3 = "test/sc-rep3.json";
    SubJobConfig config1 = createConfig("", filename1);
    SubJobConfig config2 = createConfig("", filename2);
    SubJobConfig config3 = createConfig("", filename3);

    ReportRecorderMock recordedReports = new ReportRecorderMock();
    InspectionReport inspectionReport = readSonarReport(recordedReports, config1, config2, config3);
    Assertions.assertNotNull(inspectionReport);
    Assertions.assertNotNull(inspectionReport.getIssues());
    Assertions.assertEquals(3, inspectionReport.getIssues().size());
    ReportDataChecker.checkFile(filename1, recordedReports.getRawReport(config1));
    ReportDataChecker.checkFile(filename2, recordedReports.getRawReport(config2));
    ReportDataChecker.checkFile(filename3, recordedReports.getRawReport(config3));
  }

  protected InspectionReport readSonarReport(ReportRecorder reportRecorder, SubJobConfig... configs)
      throws IOException, InterruptedException {
    SonarConnector connector = new SonarConnector(null, buildStrategy(configs), null, null);
    return connector.readSonarReports(new FilePath(new File("")), reportRecorder);
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
}
