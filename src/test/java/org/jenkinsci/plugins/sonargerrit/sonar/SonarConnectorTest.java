package org.jenkinsci.plugins.sonargerrit.sonar;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    SonarConnector connector = readSonarReport(recordedReports, config);
    Assertions.assertNotNull(connector);
    Assertions.assertNotNull(connector.getIssues());
    Assertions.assertEquals(1, connector.getIssues().size());
    ReportDataChecker.checkFile(filename, recordedReports.getRawReport(config));
  }

  @Test
  public void testReadTwoSimpleReports() throws IOException, InterruptedException {
    String filename1 = "sc-rep1.json";
    String filename2 = "sc-rep2.json";
    SubJobConfig config1 = createConfig("", filename1);
    SubJobConfig config2 = createConfig("", filename2);

    ReportRecorderMock recordedReports = new ReportRecorderMock();
    SonarConnector connector = readSonarReport(recordedReports, config1, config2);
    Assertions.assertNotNull(connector);
    Assertions.assertNotNull(connector.getIssues());
    Assertions.assertEquals(2, connector.getIssues().size());
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
    SonarConnector connector = readSonarReport(recordedReports, config1, config2, config3);
    Assertions.assertNotNull(connector);
    Assertions.assertNotNull(connector.getIssues());
    Assertions.assertEquals(3, connector.getIssues().size());
    ReportDataChecker.checkFile(filename1, recordedReports.getRawReport(config1));
    ReportDataChecker.checkFile(filename2, recordedReports.getRawReport(config2));
    ReportDataChecker.checkFile(filename3, recordedReports.getRawReport(config3));
  }

  protected SonarConnector readSonarReport(
      SonarConnector.ReportRecorder reportRecorder, SubJobConfig... configs)
      throws IOException, InterruptedException {
    SonarConnector connector = new SonarConnector(reportRecorder);
    connector.readSonarReports(
        null, buildInspectionConfig(configs), null, new FilePath(new File("")));
    return connector;
  }

  private InspectionConfig buildInspectionConfig(SubJobConfig... configs) {
    InspectionConfig config = new InspectionConfig();
    config.setType(InspectionConfig.DescriptorImpl.MULTI_TYPE);
    config.setSubJobConfigs(Arrays.asList(configs));
    return config;
  }

  private SubJobConfig createConfig(String ppath, String spath) {
    return new SubJobConfig(ppath, RESOURCES_PATH_PREFIX + spath);
  }
}
