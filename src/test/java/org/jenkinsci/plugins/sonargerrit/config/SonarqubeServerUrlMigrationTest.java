package org.jenkinsci.plugins.sonargerrit.config;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.model.FreeStyleProject;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class SonarqubeServerUrlMigrationTest {

  @Test
  @DisplayName("Migration")
  void test1(JenkinsRule jenkinsRule) throws IOException {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    try (InputStream inputStream = openFile("legacy-config.xml")) {
      project.updateByXml((Source) new StreamSource(inputStream));
    }
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
        .contains("https://sonarqube.example.org");

    assertThat(inspectionConfig.getServerURL()).isEqualTo("https://sonarqube.example.org");
  }

  @Test
  @DisplayName("setServerURL")
  void test2() {
    InspectionConfig inspectionConfig = new InspectionConfig();
    inspectionConfig.setServerURL("https://sonarqube.example.com");

    String sonarInstallationName = inspectionConfig.getSonarQubeInstallationName();
    assertThat(sonarInstallationName).isNotNull();
    assertThat(Stream.of(SonarGlobalConfiguration.get().getInstallations()))
        .filteredOn(installation -> sonarInstallationName.equals(installation.getName()))
        .hasSize(1)
        .map(SonarInstallation::getServerUrl)
        .contains("https://sonarqube.example.com");
    assertThat(inspectionConfig.getServerURL()).isEqualTo("https://sonarqube.example.com");
  }

  private InputStream openFile(String name) {
    return getClass()
        .getResourceAsStream(SonarqubeServerUrlMigrationTest.class.getSimpleName() + "/" + name);
  }
}
