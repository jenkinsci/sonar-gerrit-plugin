package org.jenkinsci.plugins.sonargerrit.data.converter;

import hudson.FilePath;
import org.junit.Assert;
import org.jenkinsci.plugins.sonargerrit.data.SonarReportBuilder;
import org.jenkinsci.plugins.sonargerrit.data.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.data.entity.Report;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 14:13
 *
 */
public class CustomReportFormatterTest {
    private static String SUCCESS_TEXT = "SonarQube violations have not been found.";
    private static String FAIL_TEXT = "<total_count> SonarQube violations have been found.\n" +
            "Info: <info_count>\n" +
            "Minor: <minor_count>\n" +
            "Major: <major_count>\n" +
            "Critical: <critical_count>\n" +
            "Blocker: <blocker_count>\n" +
            "Minor or harder: <min_minor_count>\n" +
            "Major or harder: <min_major_count>\n" +
            "Critical or harder: <min_critical_count>";

    @Test
    public void testSuccess() throws IOException, InterruptedException, URISyntaxException {
        List<Issue> i = new ArrayList<Issue>();
        String expectedResult = "SonarQube violations have not been found.";
        CustomReportFormatter basicIssueConverter = new CustomReportFormatter(i, FAIL_TEXT, SUCCESS_TEXT);
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
    }

    @Test
    public void testFail() throws IOException, InterruptedException, URISyntaxException {
        List<Issue> i = getIssues();
        String expectedResult = "19 SonarQube violations have been found.\n" +
                "Info: 1\n" +
                "Minor: 6\n" +
                "Major: 10\n" +
                "Critical: 1\n" +
                "Blocker: 1\n" +
                "Minor or harder: 18\n" +
                "Major or harder: 12\n" +
                "Critical or harder: 2";
        CustomReportFormatter basicIssueConverter = new CustomReportFormatter(i, FAIL_TEXT, SUCCESS_TEXT);
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
    }

    @Test
    public void testSuccessEmpty() throws IOException, InterruptedException, URISyntaxException {
        List<Issue> i = new ArrayList<Issue>();
        String expectedResult = "SonarQube violations have not been found.";
        CustomReportFormatter basicIssueConverter = new CustomReportFormatter(i, "", "");
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
        basicIssueConverter = new CustomReportFormatter(i, null, null);
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
    }

    @Test
    public void testFailEmpty() throws IOException, InterruptedException, URISyntaxException {
        List<Issue> i = getIssues();
        String expectedResult = "19 SonarQube violations have been found.";
        CustomReportFormatter basicIssueConverter = new CustomReportFormatter(i, "", "");
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
        basicIssueConverter = new CustomReportFormatter(i, null, null);
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
    }

    private List<Issue> getIssues() throws URISyntaxException, IOException, InterruptedException {
        URL url = getClass().getClassLoader().getResource("filter.json");

        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        Report rep = new SonarReportBuilder().fromJson(json);

        return rep.getIssues();
    }
}
