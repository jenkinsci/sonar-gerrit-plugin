package org.jenkinsci.plugins.sonargerrit.config;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.model.FreeStyleProject;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class InspectionConfigTest {

  @Test
  @DisplayName("sonarQubeInstallationName round trip")
  void test1(JenkinsRule jenkinsRule) throws Exception {
    SonarQubeInstallations.get().create(UUID.randomUUID().toString());
    String sonarQubeInstall =
        SonarQubeInstallations.get().create(UUID.randomUUID().toString()).getName();

    FreeStyleProject project = jenkinsRule.createFreeStyleProject();

    SonarToGerritPublisher before = new SonarToGerritPublisher();
    before.getInspectionConfig().setSonarQubeInstallationName(sonarQubeInstall);
    project.getPublishersList().add(before);

    jenkinsRule.submit(
        jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

    SonarToGerritPublisher after = project.getPublishersList().get(SonarToGerritPublisher.class);
    assertThat(after.getInspectionConfig().getSonarQubeInstallationName())
        .isEqualTo(sonarQubeInstall);
  }
}
