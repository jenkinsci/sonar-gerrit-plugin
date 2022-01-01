package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.gerrit.extensions.restapi.RestApiException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.junit.jupiter.api.Test;

public class ProjectPathToGerritChangedLinesMatchTest
    extends CustomProjectPathAndFilePredicateMatchTest {

  @Test
  public void testFilterIssuesByChangedFilesSubModuleNoPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig("", SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    performTest(config, false, false);
  }

  @Test
  public void testFilterIssuesByChangedFilesSubModuleAutoPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig("", SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    config.setAutoMatch(true);
    performTest(config, false, true);
  }

  @Test
  public void testFilterIssuesByChangedFilesSubModuleManualPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig(PREFIX, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    performTest(config, true, true);
  }

  @Override
  protected IssueFilterConfig createFilterConfig() {
    return new IssueFilterConfig(Severity.INFO.toString(), false, true);
  }
}
