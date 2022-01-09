package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.assertj.core.api.Assertions.assertThat;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.util.Secret;
import java.io.IOException;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** @author Réda Housni Alaoui */
@EnableJenkinsRule
class GerritHttpCredentialsTest {

  @Test
  @DisplayName(
      "Credentials that were not authored by the plugin are filtered out from the migration")
  void test1() {
    String username = UUID.randomUUID().toString();
    String password = UUID.randomUUID().toString();
    String foreignCredentialsId = createCredentials(username, password);

    String newCredentialsId =
        GerritHttpCredentials.get()
            .migrate(username, Secret.fromString(password))
            .orElseThrow(RuntimeException::new);

    assertThat(newCredentialsId).isNotEqualTo(foreignCredentialsId);
  }

  @Test
  @DisplayName("Credentials that were authored by the plugin are used by the migration")
  void test2() {
    String username = UUID.randomUUID().toString();
    String password = UUID.randomUUID().toString();
    String existingCredentialsId =
        GerritHttpCredentials.get().create(username, Secret.fromString(password)).getId();

    String newCredentialsId =
        GerritHttpCredentials.get()
            .migrate(username, Secret.fromString(password))
            .orElseThrow(RuntimeException::new);

    assertThat(newCredentialsId).isEqualTo(existingCredentialsId);
  }

  private String createCredentials(String username, String password) {
    SystemCredentialsProvider credentialsProvider = SystemCredentialsProvider.getInstance();
    String credentialsId = UUID.randomUUID().toString();
    credentialsProvider
        .getCredentials()
        .add(
            new UsernamePasswordCredentialsImpl(
                CredentialsScope.GLOBAL, credentialsId, null, username, password));
    try {
      credentialsProvider.save();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return credentialsId;
  }
}
