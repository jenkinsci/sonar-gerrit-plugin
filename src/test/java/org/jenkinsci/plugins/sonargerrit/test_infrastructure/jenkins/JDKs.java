package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import hudson.model.JDK;
import hudson.tools.InstallSourceProperty;
import hudson.tools.ZipExtractionInstaller;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import jenkins.model.Jenkins;

/** @author Réda Housni Alaoui */
public class JDKs {

  private final Jenkins jenkins;

  public JDKs(Jenkins jenkins) {
    this.jenkins = jenkins;
  }

  public String addJdk8() throws IOException {
    return addJdk(
        "https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u312-b07/OpenJDK8U-jdk_x64_linux_hotspot_8u312b07.tar.gz",
        "jdk8u312-b07");
  }

  public String addJdk17() throws IOException {
    return addJdk(
        "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_linux_hotspot_17.0.1_12.tar.gz",
        "jdk-17.0.1+12");
  }

  private String addJdk(String url, String subdir) throws IOException {
    ZipExtractionInstaller installer = new ZipExtractionInstaller(null, url, subdir);

    String name = UUID.randomUUID().toString();
    JDK jdk =
        new JDK(
            name,
            null,
            Collections.singletonList(
                new InstallSourceProperty(Collections.singletonList(installer))));
    jenkins.getJDKs().add(jdk);
    jenkins.save();

    return name;
  }
}
