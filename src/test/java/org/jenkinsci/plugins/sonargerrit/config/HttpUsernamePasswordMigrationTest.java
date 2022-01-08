package org.jenkinsci.plugins.sonargerrit.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.model.FreeStyleProject;
import hudson.util.Secret;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class HttpUsernamePasswordMigrationTest {

  @Test
  @DisplayName("Given unknown credentials, the migration should use a new credentials id")
  void test1(JenkinsRule jenkinsRule) throws IOException {
    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    String username = UUID.randomUUID().toString();
    String password = UUID.randomUUID().toString();
    project.updateByXml(loadLegacyJobConfig(username, password));
    project.doReload();

    SonarToGerritPublisher publisher =
        project.getPublishersList().get(SonarToGerritPublisher.class);

    GerritAuthenticationConfig authConfig = publisher.getAuthConfig();

    assertThat(authConfig).isNotNull();
    assertThat(authConfig.getHttpCredentialsId()).isNotNull();

    StandardUsernamePasswordCredentials credentials =
        authConfig.getHttpCredentials(null).orElseThrow(RuntimeException::new);
    assertThat(credentials.getUsername()).isEqualTo(username);
    assertThat(credentials.getPassword().getPlainText()).isEqualTo(password);
    assertThat(credentials.getId()).isEqualTo(authConfig.getHttpCredentialsId());
  }

  @Test
  @DisplayName("Given known credentials, the migration should use the existing credentials")
  void test2(JenkinsRule jenkinsRule) throws IOException {
    String username = UUID.randomUUID().toString();
    String password = UUID.randomUUID().toString();
    String credentialsId =
        GerritHttpCredentials.get().create(username, Secret.fromString(password)).getId();

    FreeStyleProject project = jenkinsRule.createFreeStyleProject();
    project.updateByXml(loadLegacyJobConfig(username, password));
    project.doReload();

    SonarToGerritPublisher publisher =
        project.getPublishersList().get(SonarToGerritPublisher.class);
    GerritAuthenticationConfig authConfig = publisher.getAuthConfig();
    assertThat(authConfig.getHttpCredentialsId()).isEqualTo(credentialsId);
  }

  private Source loadLegacyJobConfig(String username, String password) throws IOException {
    String xml;
    try (InputStream inputStream =
        getClass().getResourceAsStream(getClass().getSimpleName() + "/legacy-job-config.xml")) {
      xml = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
    }
    return new StreamSource(
        new StringReader(
            String.format(xml, username, Secret.fromString(password).getEncryptedValue())));
  }
}
