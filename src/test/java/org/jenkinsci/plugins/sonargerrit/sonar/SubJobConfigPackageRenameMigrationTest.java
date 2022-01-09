package org.jenkinsci.plugins.sonargerrit.sonar;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.model.FreeStyleProject;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class SubJobConfigPackageRenameMigrationTest {

  @Test
  void test(JenkinsRule jenkinsRule) throws IOException {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    String legacySonarReportPath = UUID.randomUUID().toString();
    project.updateByXml(loadLegacyJobConfig(legacySonarReportPath));
    project.doReload();

    SonarToGerritPublisher publisher =
        project.getPublishersList().get(SonarToGerritPublisher.class);

    assertThat(publisher.getInspectionConfig().getSubJobConfigs())
        .hasSize(1)
        .map(SubJobConfig::getSonarReportPath)
        .contains(legacySonarReportPath);
  }

  private Source loadLegacyJobConfig(String sonarReportPath) throws IOException {
    String xml;
    try (InputStream inputStream =
        getClass().getResourceAsStream(getClass().getSimpleName() + "/legacy-job-config.xml")) {
      xml = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
    }
    return new StreamSource(new StringReader(String.format(xml, sonarReportPath)));
  }
}
