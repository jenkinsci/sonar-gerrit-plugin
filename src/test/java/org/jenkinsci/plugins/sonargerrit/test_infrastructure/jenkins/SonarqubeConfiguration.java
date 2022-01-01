package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import static java.util.Objects.requireNonNull;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import hudson.util.Secret;
import java.io.IOException;
import java.util.UUID;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.ArrayUtils;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;

/** @author Réda Housni Alaoui */
public class SonarqubeConfiguration {

  private final Jenkins jenkins;

  public SonarqubeConfiguration(Jenkins jenkins) {
    this.jenkins = jenkins;
  }

  public void addInstallation(String name, String url, String token) throws IOException {
    SystemCredentialsProvider credentialsProvider =
        jenkins
            .getExtensionList(SystemCredentialsProvider.class)
            .get(SystemCredentialsProvider.class);
    requireNonNull(credentialsProvider);

    String credentialsId = UUID.randomUUID().toString();
    Credentials credentials =
        new StringCredentialsImpl(
            CredentialsScope.GLOBAL, credentialsId, null, Secret.fromString(token));
    credentialsProvider.getCredentials().add(credentials);
    credentialsProvider.save();

    SonarGlobalConfiguration globalConfiguration =
        jenkins.getDescriptorList(GlobalConfiguration.class).get(SonarGlobalConfiguration.class);
    requireNonNull(globalConfiguration);
    globalConfiguration.setBuildWrapperEnabled(true);
    SonarInstallation sonarInstallation =
        new SonarInstallation(name, url, credentialsId, null, null, null, null, null, null);
    globalConfiguration.setInstallations(
        ArrayUtils.add(globalConfiguration.getInstallations(), sonarInstallation));
  }
}
