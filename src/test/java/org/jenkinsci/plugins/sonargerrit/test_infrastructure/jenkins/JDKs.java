package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import hudson.model.JDK;
import hudson.tools.InstallSourceProperty;
import hudson.tools.ZipExtractionInstaller;
import java.io.IOException;
import java.util.Collections;
import jenkins.model.Jenkins;

/** @author Réda Housni Alaoui */
public class JDKs {

  private final Jenkins jenkins;

  public JDKs(Jenkins jenkins) {
    this.jenkins = jenkins;
  }

  public void add(String name) throws IOException {
    ZipExtractionInstaller installer =
        new ZipExtractionInstaller(
            null,
            "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_linux_hotspot_17.0.1_12.tar.gz",
            "jdk-17.0.1+12");

    JDK jdk =
        new JDK(
            name,
            null,
            Collections.singletonList(
                new InstallSourceProperty(Collections.singletonList(installer))));
    jenkins.getJDKs().add(jdk);
    jenkins.save();
  }
}
