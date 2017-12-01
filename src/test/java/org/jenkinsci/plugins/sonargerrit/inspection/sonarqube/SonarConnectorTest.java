package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import hudson.FilePath;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.ReportDataChecker;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 30.11.2017 15:10
 * <p/>
 * $Id$
 */
public class SonarConnectorTest {
    public static final String RESOURCES_PATH_PREFIX = "src/test/resources/";

    @Test
    public void testReadSingleSimpleReport() throws IOException, InterruptedException {
        String filename = "one_issue.json";
        SubJobConfig config = createConfig("", "one_issue.json");

        SonarConnector connector = readSonarReport(config);
        Assert.assertNotNull(connector);
        Assert.assertNotNull(connector.getIssues());
        Assert.assertEquals(1, connector.getIssues().size());
        ReportDataChecker.checkFile(filename, connector.getRawReport(config));

    }

    @Test
    public void testReadTwoSimpleReports() throws IOException, InterruptedException {
        String filename1 = "sc-rep1.json";
        String filename2 = "sc-rep2.json";
        SubJobConfig config1 = createConfig("", filename1);
        SubJobConfig config2 = createConfig("", filename2);

        SonarConnector connector = readSonarReport(config1, config2);
        Assert.assertNotNull(connector);
        Assert.assertNotNull(connector.getIssues());
        Assert.assertEquals(2, connector.getIssues().size());
        ReportDataChecker.checkFile(filename1, connector.getRawReport(config1));
        ReportDataChecker.checkFile(filename2, connector.getRawReport(config2));

    }

    @Test
    public void testReadThreeSimpleReports() throws IOException, InterruptedException {
        String filename1 = "sc-rep1.json";
        String filename2 = "sc-rep2.json";
        String filename3 = "test/sc-rep3.json";
        SubJobConfig config1 = createConfig("", filename1);
        SubJobConfig config2 = createConfig("", filename2);
        SubJobConfig config3 = createConfig("", filename3);

        SonarConnector connector = readSonarReport(config1, config2, config3);
        Assert.assertNotNull(connector);
        Assert.assertNotNull(connector.getIssues());
        Assert.assertEquals(3, connector.getIssues().size());
        ReportDataChecker.checkFile(filename1, connector.getRawReport(config1));
        ReportDataChecker.checkFile(filename2, connector.getRawReport(config2));
        ReportDataChecker.checkFile(filename3, connector.getRawReport(config3));

    }

    protected SonarConnector readSonarReport(SubJobConfig... configs) throws IOException, InterruptedException {
        SonarConnector connector = new SonarConnector(null, Arrays.asList(configs));
        connector.readSonarReports(new FilePath(new File("")));
        return connector;
    }

    private SubJobConfig createConfig(String ppath, String spath) {
        return new SubJobConfig(ppath, RESOURCES_PATH_PREFIX + spath);
    }

}
