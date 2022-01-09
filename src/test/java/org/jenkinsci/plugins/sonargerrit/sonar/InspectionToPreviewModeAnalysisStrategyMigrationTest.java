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
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class InspectionToPreviewModeAnalysisStrategyMigrationTest {

  @Test
  void test(JenkinsRule jenkinsRule) throws IOException {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();

    String legacyProjectPath = UUID.randomUUID().toString();
    String legacySonarReportPath = UUID.randomUUID().toString();
    boolean autoMatch = true;

    project.updateByXml(loadLegacyJobConfig(legacyProjectPath, legacySonarReportPath, autoMatch));
    project.doReload();

    SonarToGerritPublisher publisher =
        project.getPublishersList().get(SonarToGerritPublisher.class);

    AnalysisStrategy analysisStrategy = publisher.getInspectionConfig().getAnalysisStrategy();
    assertThat(analysisStrategy).isInstanceOf(PreviewModeAnalysisStrategy.class);
    PreviewModeAnalysisStrategy strategy = (PreviewModeAnalysisStrategy) analysisStrategy;

    SubJobConfig baseConfig = strategy.getBaseConfig();
    assertThat(baseConfig.getProjectPath()).isEqualTo(legacyProjectPath);
    assertThat(baseConfig.getSonarReportPath()).isEqualTo(legacySonarReportPath);
    assertThat(baseConfig.isAutoMatch()).isTrue();
  }

  private Source loadLegacyJobConfig(String projectPath, String sonarReportPath, boolean autoMatch)
      throws IOException {
    String xml;
    try (InputStream inputStream =
        getClass().getResourceAsStream(getClass().getSimpleName() + "/legacy-job-config.xml")) {
      xml = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
    }
    return new StreamSource(
        new StringReader(String.format(xml, projectPath, sonarReportPath, autoMatch)));
  }
}
