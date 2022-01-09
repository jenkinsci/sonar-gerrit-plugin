package org.jenkinsci.plugins.sonargerrit.review.formatter;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarQubeIssue;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarReportBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 13:05 */
public class CustomIssueFormatterTest {
  @Test
  public void testKnownUrl() throws IOException, InterruptedException, URISyntaxException {
    IssueAdapter i = getIssue();
    String text = "<severity> SonarQube violation:\n\n\n<message>\n\n\nRead more: <rule_url>";
    String expectedResult =
        "MINOR SonarQube violation:\n\n\nRemove this unused import 'com.turquoise.juice.property.PropertiesHandler'.\n\n\nRead more: http://localhost:9000/coding_rules#rule_key=squid%3AUselessImportCheck";
    CustomIssueFormatter basicIssueConverter =
        new CustomIssueFormatter(i, text, "http://localhost:9000");
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
  }

  @Test
  public void testUnknownUrl() throws IOException, InterruptedException, URISyntaxException {
    IssueAdapter i = getIssue();
    String text = "<severity> SonarQube violation:\n\n\n<message>\n\n\nRead more: <rule_url>";
    String expectedResult =
        "MINOR SonarQube violation:\n\n\nRemove this unused import 'com.turquoise.juice.property.PropertiesHandler'.\n\n\nRead more: squid:UselessImportCheck";
    CustomIssueFormatter basicIssueConverter = new CustomIssueFormatter(i, text, null);
    Assertions.assertEquals(expectedResult, basicIssueConverter.getMessage());
  }

  private IssueAdapter getIssue() throws URISyntaxException, IOException, InterruptedException {
    URL url = getClass().getClassLoader().getResource("filter.json");

    File path = new File(Objects.requireNonNull(url).toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();
    Report rep = new SonarReportBuilder().fromJson(json);

    return new SonarQubeIssue(rep.getIssues().get(0), null, new SubJobConfig());
  }
}
