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
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.SonarScannerConfiguration;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.SonarqubeConfiguration;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.SonarqubeAccessTokens;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.SonarqubeServer;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
public class Cluster {

  private final GerritServer gerrit;
  private final SonarqubeServer sonarqube;
  private final JenkinsRule jenkinsRule;

  private final String jenkinsGerritCredentialsId;
  private final String jenkinsJdk8InstallationName;
  private final String jenkinsMavenInstallationName;
  private final String jenkinsSonarScannerInstallationName;
  private final String jenkinsSonarqubeInstallationName;
  private final String jenkinsGerritTriggerServerName;

  private Cluster(GerritServer gerrit, SonarqubeServer sonarqube, JenkinsRule jenkinsRule)
      throws IOException {
    this.gerrit = requireNonNull(gerrit);
    this.sonarqube = requireNonNull(sonarqube);
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

    jenkinsSonarScannerInstallationName =
        new SonarScannerConfiguration(jenkinsRule.jenkins).addInstallation();

    String sonarqubeToken =
        new SonarqubeAccessTokens(sonarqube).createAdminAccessToken(UUID.randomUUID().toString());
    jenkinsSonarqubeInstallationName =
        new SonarqubeConfiguration(jenkinsRule.jenkins)
            .addInstallation(sonarqube.url(), sonarqubeToken);
  }

  public static Cluster configure(
      GerritServer gerrit, SonarqubeServer sonarqube, JenkinsRule jenkinsRule) throws IOException {
    return new Cluster(gerrit, sonarqube, jenkinsRule);
  }

  public String jenkinsGerritCredentialsId() {
    return jenkinsGerritCredentialsId;
  }

  public GerritServer gerrit() {
    return gerrit;
  }

  public SonarqubeServer sonarqube() {
    return sonarqube;
  }

  public JenkinsRule jenkinsRule() {
    return jenkinsRule;
  }

  public String jenkinsMavenInstallationName() {
    return jenkinsMavenInstallationName;
  }

  public String jenkinsSonarScannerInstallationName() {
    return jenkinsSonarScannerInstallationName;
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
