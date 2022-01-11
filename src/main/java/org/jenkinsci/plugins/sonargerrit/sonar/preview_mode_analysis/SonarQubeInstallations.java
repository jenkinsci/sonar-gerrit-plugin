package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** @author Réda Housni Alaoui */
@Restricted(NoExternalUse.class)
public class SonarQubeInstallations {

  private SonarQubeInstallations() {}

  public static SonarQubeInstallations get() {
    return new SonarQubeInstallations();
  }

  public SonarInstallation findOrCreate(String serverUrl) {
    return Stream.of(SonarGlobalConfiguration.get().getInstallations())
        .filter(installation -> serverUrl.equals(installation.getServerUrl()))
        .findFirst()
        .orElseGet(() -> create(serverUrl));
  }

  public Optional<SonarInstallation> byName(String name) {
    return Stream.of(SonarGlobalConfiguration.get().getInstallations())
        .filter(installation -> name.equals(installation.getName()))
        .findFirst();
  }

  public SonarInstallation create(String serverUrl) {
    SonarInstallation sonarInstallation =
        new SonarInstallation(
            UUID.randomUUID().toString(), serverUrl, null, null, null, null, null, null, null);

    SonarGlobalConfiguration.get()
        .setInstallations(
            ArrayUtils.add(SonarGlobalConfiguration.get().getInstallations(), sonarInstallation));
    return sonarInstallation;
  }
}
