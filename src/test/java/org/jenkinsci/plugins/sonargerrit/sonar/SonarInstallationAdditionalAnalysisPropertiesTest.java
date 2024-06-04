package org.jenkinsci.plugins.sonargerrit.sonar;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.plugins.sonar.SonarInstallation;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** @author Réda Housni Alaoui */
class SonarInstallationAdditionalAnalysisPropertiesTest {

  @Test
  @DisplayName("Get duration")
  void test1() {
    assertThat(
            SonarInstallationAdditionalAnalysisProperties.parse(
                    createSonarInstallation(
                        "foo=bar sonar.ws.timeout=6 bar=foo sonar.ws.timeout=600"))
                .getDuration("sonar.ws.timeout", ChronoUnit.SECONDS))
        .contains(Duration.of(600, ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("Get empty duration")
  void test2() {
    assertThat(
            SonarInstallationAdditionalAnalysisProperties.parse(
                    createSonarInstallation("sonar.ws.timeout= =foo"))
                .getDuration("sonar.ws.timeout", ChronoUnit.SECONDS))
        .isEmpty();
  }

  private SonarInstallation createSonarInstallation(String additionalAnalysisProperties) {
    return new SonarInstallation(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        null,
        null,
        null,
        null,
        null,
        additionalAnalysisProperties,
        null);
  }
}
