package org.jenkinsci.plugins.sonargerrit.gerrit;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.Reports;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 14:13 */
public class CustomReportFormatterTest {
  private static final String SUCCESS_TEXT = "SonarQube violations have not been found.";
  private static final String FAIL_TEXT =
                  """
                  <total_count> SonarQube violations have been found.
                  Info: <info_count>
                  Minor: <minor_count>
                  Major: <major_count>
                  Critical: <critical_count>
                  Blocker: <blocker_count>
                  Minor or harder: <min_minor_count>
                  Major or harder: <min_major_count>
                  Critical or harder: <min_critical_count>""";

  @Test
  public void testSuccess() {
    List<Issue> i = new ArrayList<>();
    String expectedResult = "SonarQube violations have not been found.";
    CustomReportFormatter basicIssueConverter =
        new CustomReportFormatter(i, FAIL_TEXT, SUCCESS_TEXT);
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
  }

  @Test
  public void testFail() throws IOException, InterruptedException, URISyntaxException {
    List<Issue> i = getIssues();
    String expectedResult =
                    """
                    19 SonarQube violations have been found.
                    Info: 1
                    Minor: 6
                    Major: 10
                    Critical: 1
                    Blocker: 1
                    Minor or harder: 18
                    Major or harder: 12
                    Critical or harder: 2""";
    CustomReportFormatter basicIssueConverter =
        new CustomReportFormatter(i, FAIL_TEXT, SUCCESS_TEXT);
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
  }

  @Disabled
  @Test
  public void testSuccessEmpty() {
    List<Issue> i = new ArrayList<>();
    String expectedResult = "SonarQube violations have not been found.";
    CustomReportFormatter basicIssueConverter = new CustomReportFormatter(i, "", "");
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
    basicIssueConverter = new CustomReportFormatter(i, null, null);
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
  }

  @Disabled
  @Test
  public void testFailEmpty() throws IOException, InterruptedException, URISyntaxException {
    List<Issue> i = getIssues();
    String expectedResult = "19 SonarQube violations have been found.";
    CustomReportFormatter basicIssueConverter = new CustomReportFormatter(i, "", "");
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
    basicIssueConverter = new CustomReportFormatter(i, null, null);
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
  }

  private List<Issue> getIssues() throws URISyntaxException, IOException, InterruptedException {
    URL url = getClass().getClassLoader().getResource("filter.json");

    File path = new File(Objects.requireNonNull(url).toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();
    return Reports.loadIssues(json);
  }
}
