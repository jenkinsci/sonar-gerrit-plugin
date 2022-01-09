package org.jenkinsci.plugins.sonargerrit.sonar;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 12.06.2015 13:52 */
public class SonarReportBuilderTest {

  @Test
  public void testLargeFile() throws URISyntaxException, IOException, InterruptedException {
    URL url = getClass().getClassLoader().getResource("example.json");

    File path = new File(Objects.requireNonNull(url).toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();
    Report rep = new SonarReportBuilder().fromJson(json);
    Assertions.assertNotNull(rep);
    Assertions.assertNotNull(rep.getComponents());
    Assertions.assertNotNull(rep.getIssues());
    Assertions.assertEquals(169, rep.getComponents().size());
    Assertions.assertEquals(177, rep.getIssues().size());
  }

  @Test
  public void testSmallFile() throws URISyntaxException, IOException, InterruptedException {
    String filename = "one_issue.json";
    URL url = getClass().getClassLoader().getResource(filename);
    File path = new File(Objects.requireNonNull(url).toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();
    Report report = new SonarReportBuilder().fromJson(json);
    ReportDataChecker.checkFile(filename, report);
  }
}
