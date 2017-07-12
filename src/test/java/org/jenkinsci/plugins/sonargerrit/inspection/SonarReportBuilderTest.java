package org.jenkinsci.plugins.sonargerrit.inspection;

import hudson.FilePath;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.*;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarReportBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 12.06.2015 13:52
 *
 */
public class SonarReportBuilderTest {

    @Test
    public void testLargeFile() throws URISyntaxException, IOException, InterruptedException {
        URL url = getClass().getClassLoader().getResource("example.json");

        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        Report rep = new SonarReportBuilder().fromJson(json);
        Assert.assertNotNull(rep);
        Assert.assertNotNull(rep.getComponents());
        Assert.assertNotNull(rep.getIssues());
        Assert.assertNotNull(rep.getRules());
        Assert.assertNotNull(rep.getUsers());
        Assert.assertEquals(169, rep.getComponents().size());
        Assert.assertEquals(177, rep.getIssues().size());
        Assert.assertEquals(27, rep.getRules().size());
        Assert.assertEquals(0, rep.getUsers().size());
    }

    @Test
    public void testSmallFile() throws URISyntaxException, IOException, InterruptedException {
        String filename = "one_issue.json";
        URL url = getClass().getClassLoader().getResource(filename);
        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        Report report = new SonarReportBuilder().fromJson(json);
        ReportDataChecker.checkFile(filename, report);
    }
}
