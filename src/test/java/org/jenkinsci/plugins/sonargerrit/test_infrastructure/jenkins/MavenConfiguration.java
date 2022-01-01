package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import hudson.tasks.Maven;
import hudson.tools.InstallSourceProperty;
import hudson.tools.ZipExtractionInstaller;
import java.io.IOException;
import java.util.Collections;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.ArrayUtils;

/** @author Réda Housni Alaoui */
public class MavenConfiguration {

  private final Jenkins jenkins;

  public MavenConfiguration(Jenkins jenkins) {
    this.jenkins = jenkins;
  }

  public void addInstallation(String name) throws IOException {
    ZipExtractionInstaller installer =
        new ZipExtractionInstaller(
            null,
            "https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz",
            "apache-maven-3.8.4");

    Maven.MavenInstallation mavenInstallation =
        new Maven.MavenInstallation(
            name,
            null,
            Collections.singletonList(
                new InstallSourceProperty(Collections.singletonList(installer))));

    Maven.DescriptorImpl mavenDescriptor = jenkins.getDescriptorByType(Maven.DescriptorImpl.class);
    mavenDescriptor.setInstallations(
        ArrayUtils.add(mavenDescriptor.getInstallations(), mavenInstallation));
  }
}
