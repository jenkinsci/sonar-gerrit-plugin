package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import java.io.IOException;
import java.net.URISyntaxException;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 05.12.2017 18:52
 *
 * <p>$Id$
 */

/*
 * Slightly different approach to performTest ComponentPathBuilder
 * The more tests, the better :)
 */
public class ProjectPathToGerritFilenameMatchTest
    extends CustomProjectPathAndFilePredicateMatchTest {

  public static final String EXTRA_FILENAME_IN_GERRIT = "test/" + FILENAME_IN_GERRIT;

  @Test
  public void testFilterIssuesByChangedFilesSubModuleNoPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig(
            SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH,
            SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    getReport(config, false);
    performTest(config, false, false);
  }

  @Test
  public void testFilterIssuesByChangedFilesSubModuleWithPathCorrection()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig(
            SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH,
            SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    config.setAutoMatch(true);
    performTest(config, false, true);
  }

  @Test
  public void testFilterIssuesByChangedFilesSubModuleWithSubConfig()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {

    SubJobConfig config =
        new SubJobConfig(PREFIX, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    performTest(config, true, true);
  }

  @Test
  public void testFilterIssuesByChangedFilesSubModuleMultiMatch()
      throws URISyntaxException, IOException, InterruptedException, RestApiException {
    SubJobConfig config =
        new SubJobConfig(
            SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH,
            SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    config.setAutoMatch(true);

    performTest(config, false, false, EXTRA_FILENAME_IN_GERRIT);
  }

  @Override
  protected IssueFilterConfig createFilterConfig() {
    return new IssueFilterConfig(Severity.INFO.toString(), false, false);
  }
}
