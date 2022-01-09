package org.jenkinsci.plugins.sonargerrit.sonar;

import java.io.IOException;
import java.net.URISyntaxException;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.jupiter.api.Test;

public class ProjectPathToGerritChangedLinesMatchTest
    extends CustomProjectPathAndFilePredicateMatchTest {

  @Test
  public void testFilterIssuesByChangedFilesSubModuleNoPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig("", SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    performTest(config, false);
  }

  @Test
  public void testFilterIssuesByChangedFilesSubModuleAutoPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig("", SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    config.setAutoMatch(true);
    performTest(config, true);
  }

  @Test
  public void testFilterIssuesByChangedFilesSubModuleManualPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig(PREFIX, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    performTest(config, true);
  }

  @Override
  protected IssueFilterConfig createFilterConfig() {
    return new IssueFilterConfig(Severity.INFO.toString(), false, true);
  }
}
