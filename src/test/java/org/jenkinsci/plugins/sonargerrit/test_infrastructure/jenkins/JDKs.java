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

  public String addJdk17() throws IOException {
    return addJdk(
        "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.12_7.tar.gz",
        "jdk-17.0.12+7");
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
