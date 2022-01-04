package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import hudson.plugins.sonar.SonarRunnerInstallation;
import hudson.plugins.sonar.SonarRunnerInstaller;
import hudson.tools.InstallSourceProperty;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.ArrayUtils;

/** @author Réda Housni Alaoui */
public class SonarScannerConfiguration {

  private final Jenkins jenkins;

  public SonarScannerConfiguration(Jenkins jenkins) {
    this.jenkins = jenkins;
  }

  public String addInstallation() throws IOException {
    SonarRunnerInstaller installer = new SonarRunnerInstaller("4.6.2.2472");

    String name = UUID.randomUUID().toString();
    SonarRunnerInstallation installation =
        new SonarRunnerInstallation(
            name,
            null,
            Collections.singletonList(
                new InstallSourceProperty(Collections.singletonList(installer))));

    SonarRunnerInstallation.DescriptorImpl descriptor =
        jenkins.getDescriptorByType(SonarRunnerInstallation.DescriptorImpl.class);
    descriptor.setInstallations(ArrayUtils.add(descriptor.getInstallations(), installation));

    return name;
  }
}
