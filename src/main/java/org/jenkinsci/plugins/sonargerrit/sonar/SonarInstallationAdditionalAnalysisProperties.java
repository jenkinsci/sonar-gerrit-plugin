package org.jenkinsci.plugins.sonargerrit.sonar;

import hudson.plugins.sonar.SonarInstallation;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** @author Réda Housni Alaoui */
@Restricted(NoExternalUse.class)
public class SonarInstallationAdditionalAnalysisProperties {

  private final Map<String, String> properties;

  private SonarInstallationAdditionalAnalysisProperties(SonarInstallation sonarInstallation) {
    String additionalAnalysisProperties = sonarInstallation.getAdditionalAnalysisProperties();
    if (additionalAnalysisProperties == null) {
      properties = Collections.emptyMap();
      return;
    }

    properties =
        Stream.of(StringUtils.split(additionalAnalysisProperties))
            .map(property -> StringUtils.split(property, "="))
            .filter(keyValue -> keyValue.length == 2)
            .collect(
                Collectors.toMap(
                    keyValue -> keyValue[0],
                    keyValue -> keyValue[1],
                    (firstValue, secondValue) -> secondValue));
  }

  public static SonarInstallationAdditionalAnalysisProperties parse(
      SonarInstallation sonarInstallation) {
    return new SonarInstallationAdditionalAnalysisProperties(sonarInstallation);
  }

  public Optional<Duration> getDuration(String key, TemporalUnit temporalUnit) {
    return Optional.ofNullable(properties.get(key))
        .map(
            stringValue -> {
              try {
                return Long.parseLong(stringValue);
              } catch (NumberFormatException e) {
                return null;
              }
            })
        .map(longValue -> Duration.of(longValue, temporalUnit));
  }
}
