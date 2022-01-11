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
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.PreviewModeAnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class SonarInstallationToPreviewModeAnalysisMigrationTest {

  @Test
  @DisplayName("Sonarqube installation name is moved to preview mode analysis strategy")
  void test1(JenkinsRule jenkinsRule) throws IOException {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    String legacyName = UUID.randomUUID().toString();
    project.updateByXml(loadLegacyJobConfig(legacyName));
    project.doReload();

    SonarToGerritPublisher publisher =
        project.getPublishersList().get(SonarToGerritPublisher.class);

    Inspection inspectionConfig = publisher.getInspectionConfig();

    AnalysisStrategy analysisStrategy = inspectionConfig.getAnalysisStrategy();
    assertThat(analysisStrategy).isInstanceOf(PreviewModeAnalysisStrategy.class);
    PreviewModeAnalysisStrategy previewModeAnalysisStrategy =
        (PreviewModeAnalysisStrategy) analysisStrategy;
    assertThat(previewModeAnalysisStrategy.getSonarQubeInstallationName()).isEqualTo(legacyName);
  }

  private Source loadLegacyJobConfig(String sonarQubeInstallationName) throws IOException {
    String xml;
    try (InputStream inputStream =
        getClass().getResourceAsStream(getClass().getSimpleName() + "/legacy-job-config.xml")) {
      xml = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
    }
    return new StreamSource(new StringReader(String.format(xml, sonarQubeInstallationName)));
  }
}
