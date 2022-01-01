package org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster;

import static java.util.Objects.requireNonNull;

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

  private static final String JENKINS_MAVEN_INSTALLATION_NAME = "maven";
  private static final String JENKINS_SONAR_SCANNER_INSTALLATION_NAME = "scanner";
  private static final String JENKINS_SONARQUBE_INSTALLATION_NAME = "sonarqube";
  private static final String JENKINS_JDK_INSTALLATION_NAME = "jdk";

  private final GerritServer gerrit;
  private final SonarqubeServer sonarqube;
  private final JenkinsRule jenkinsRule;

  private Cluster(GerritServer gerrit, SonarqubeServer sonarqube, JenkinsRule jenkinsRule)
      throws IOException {
    this.gerrit = requireNonNull(gerrit);
    this.sonarqube = requireNonNull(sonarqube);
    this.jenkinsRule = requireNonNull(jenkinsRule);

    new GerritTriggerConfiguration(jenkinsRule.jenkins)
        .addServer(gerrit.url(), gerrit.adminUsername(), gerrit.adminPassword());

    new JDKs(jenkinsRule.jenkins).add(JENKINS_JDK_INSTALLATION_NAME);
    new MavenConfiguration(jenkinsRule.jenkins).addInstallation(JENKINS_MAVEN_INSTALLATION_NAME);

    new SonarScannerConfiguration(jenkinsRule.jenkins)
        .addInstallation(JENKINS_SONAR_SCANNER_INSTALLATION_NAME);

    String sonarqubeToken =
        new SonarqubeAccessTokens(sonarqube).createAdminAccessToken(UUID.randomUUID().toString());
    new SonarqubeConfiguration(jenkinsRule.jenkins)
        .addInstallation(JENKINS_SONARQUBE_INSTALLATION_NAME, sonarqube.url(), sonarqubeToken);
  }

  public static Cluster configure(
      GerritServer gerrit, SonarqubeServer sonarqube, JenkinsRule jenkinsRule) throws IOException {
    return new Cluster(gerrit, sonarqube, jenkinsRule);
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
    return JENKINS_MAVEN_INSTALLATION_NAME;
  }

  public String jenkinsSonarScannerInstallationName() {
    return JENKINS_SONAR_SCANNER_INSTALLATION_NAME;
  }

  public String jenkinsSonarqubeInstallationName() {
    return JENKINS_SONARQUBE_INSTALLATION_NAME;
  }

  public String jenkinsJdkInstallationName() {
    return JENKINS_JDK_INSTALLATION_NAME;
  }
}
