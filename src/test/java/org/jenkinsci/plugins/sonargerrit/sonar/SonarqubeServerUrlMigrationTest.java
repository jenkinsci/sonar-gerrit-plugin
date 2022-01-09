package org.jenkinsci.plugins.sonargerrit.sonar;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.model.FreeStyleProject;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class SonarqubeServerUrlMigrationTest {

  @Test
  @DisplayName("Given a unknown url, the migration should use a new sonarqube installation")
  void test1(JenkinsRule jenkinsRule) throws IOException {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    String legacyServerUrl = UUID.randomUUID().toString();
    project.updateByXml(loadLegacyJobConfig(legacyServerUrl));
    project.doReload();

    SonarToGerritPublisher publisher =
        project.getPublishersList().get(SonarToGerritPublisher.class);

    InspectionConfig inspectionConfig = publisher.getInspectionConfig();

    String sonarInstallationName = inspectionConfig.getSonarQubeInstallationName();
    assertThat(sonarInstallationName).isNotNull();

    assertThat(Stream.of(SonarGlobalConfiguration.get().getInstallations()))
        .filteredOn(installation -> sonarInstallationName.equals(installation.getName()))
        .hasSize(1)
        .map(SonarInstallation::getServerUrl)
        .contains(legacyServerUrl);

    assertThat(inspectionConfig.getServerURL()).isEqualTo(legacyServerUrl);
  }

  @Test
  @DisplayName("Given a known url, the migration should use an existing sonarqube installation")
  void test2(JenkinsRule jenkinsRule) throws IOException {
    String legacyServerUrl = UUID.randomUUID().toString();
    String installationName = SonarQubeInstallations.get().create(legacyServerUrl).getName();

    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    project.updateByXml(loadLegacyJobConfig(legacyServerUrl));
    project.doReload();

    SonarToGerritPublisher publisher =
        project.getPublishersList().get(SonarToGerritPublisher.class);
    InspectionConfig inspectionConfig = publisher.getInspectionConfig();
    assertThat(inspectionConfig.getSonarQubeInstallationName()).isEqualTo(installationName);
  }

  @Test
  @DisplayName("serverURL field is transient")
  void test3(JenkinsRule jenkinsRule) throws IOException {
    String legacyServerUrl = UUID.randomUUID().toString();
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    project.updateByXml(loadLegacyJobConfig(legacyServerUrl));
    project.doReload();

    String newServerUrl = UUID.randomUUID().toString();
    String installationName = SonarQubeInstallations.get().create(newServerUrl).getName();
    project
        .getPublishersList()
        .get(SonarToGerritPublisher.class)
        .getInspectionConfig()
        .setSonarQubeInstallationName(installationName);
    project.save();

    project.doReload();
    assertThat(
            project
                .getPublishersList()
                .get(SonarToGerritPublisher.class)
                .getInspectionConfig()
                .getServerURL())
        .isEqualTo(newServerUrl);
  }

  @Test
  @DisplayName(
      "Setting the serverURL with an unknown url should create a new sonarqube installation")
  void test4() {
    String url = UUID.randomUUID().toString();
    InspectionConfig inspectionConfig = new InspectionConfig();
    inspectionConfig.setServerURL(url);

    String sonarInstallationName = inspectionConfig.getSonarQubeInstallationName();
    assertThat(sonarInstallationName).isNotNull();
    assertThat(Stream.of(SonarGlobalConfiguration.get().getInstallations()))
        .filteredOn(installation -> sonarInstallationName.equals(installation.getName()))
        .hasSize(1)
        .map(SonarInstallation::getServerUrl)
        .contains(url);
    assertThat(inspectionConfig.getServerURL()).isEqualTo(url);
  }

  private Source loadLegacyJobConfig(String serverUrl) throws IOException {
    String xml;
    try (InputStream inputStream =
        getClass().getResourceAsStream(getClass().getSimpleName() + "/legacy-job-config.xml")) {
      xml = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
    }
    return new StreamSource(new StringReader(String.format(xml, serverUrl)));
  }
}
