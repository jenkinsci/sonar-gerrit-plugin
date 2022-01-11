package org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster;

import static java.util.Objects.requireNonNull;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import java.io.IOException;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritServer;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.GerritTriggerConfiguration;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.JDKs;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.MavenConfiguration;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.SonarqubeConfiguration;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.Sonarqube7Server;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.SonarqubeAccessTokens;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.SonarqubeServer;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
public class Cluster {

  private final GerritServer gerrit;
  private final JenkinsRule jenkinsRule;

  private final String jenkinsGerritCredentialsId;
  private final String jenkinsJdk8InstallationName;
  private final String jenkinsMavenInstallationName;
  private final String jenkinsSonarqube7InstallationName;
  private final String jenkinsSonarqubeInstallationName;
  private final String jenkinsGerritTriggerServerName;

  private Cluster(
      GerritServer gerrit,
      Sonarqube7Server sonarqube7,
      SonarqubeServer sonarqube,
      JenkinsRule jenkinsRule)
      throws IOException {
    this.gerrit = requireNonNull(gerrit);
    this.jenkinsRule = requireNonNull(jenkinsRule);

    SystemCredentialsProvider credentialsProvider = SystemCredentialsProvider.getInstance();
    jenkinsGerritCredentialsId = UUID.randomUUID().toString();
    Credentials credentials =
        new UsernamePasswordCredentialsImpl(
            CredentialsScope.GLOBAL,
            jenkinsGerritCredentialsId,
            null,
            gerrit.adminUsername(),
            gerrit.adminPassword());
    credentialsProvider.getCredentials().add(credentials);
    credentialsProvider.save();

    jenkinsJdk8InstallationName = new JDKs(jenkinsRule.jenkins).addJdk8();
    jenkinsMavenInstallationName = new MavenConfiguration(jenkinsRule.jenkins).addInstallation();

    jenkinsGerritTriggerServerName =
        new GerritTriggerConfiguration(jenkinsRule.jenkins)
            .addServer(gerrit.url(), gerrit.adminUsername(), gerrit.adminPassword());

    SonarqubeConfiguration sonarqubeConfiguration = new SonarqubeConfiguration(jenkinsRule.jenkins);

    jenkinsSonarqube7InstallationName =
        sonarqubeConfiguration.addInstallation(
            sonarqube7.url(),
            new SonarqubeAccessTokens(sonarqube7.url(), sonarqube7.adminAuthorization())
                .createAdminAccessToken(UUID.randomUUID().toString()));
    jenkinsSonarqubeInstallationName =
        sonarqubeConfiguration.addInstallation(
            sonarqube.url(),
            new SonarqubeAccessTokens(sonarqube.url(), sonarqube.adminAuthorization())
                .createAdminAccessToken(UUID.randomUUID().toString()));
  }

  public static Cluster configure(
      GerritServer gerrit,
      Sonarqube7Server sonarqube7,
      SonarqubeServer sonarqube,
      JenkinsRule jenkinsRule)
      throws IOException {
    return new Cluster(gerrit, sonarqube7, sonarqube, jenkinsRule);
  }

  public String jenkinsGerritCredentialsId() {
    return jenkinsGerritCredentialsId;
  }

  public GerritServer gerrit() {
    return gerrit;
  }

  public JenkinsRule jenkinsRule() {
    return jenkinsRule;
  }

  public String jenkinsMavenInstallationName() {
    return jenkinsMavenInstallationName;
  }

  public String jenkinsSonarqube7InstallationName() {
    return jenkinsSonarqube7InstallationName;
  }

  public String jenkinsSonarqubeInstallationName() {
    return jenkinsSonarqubeInstallationName;
  }

  public String jenkinsJdk8InstallationName() {
    return jenkinsJdk8InstallationName;
  }

  public String jenkinsGerritTriggerServerName() {
    return jenkinsGerritTriggerServerName;
  }
}
