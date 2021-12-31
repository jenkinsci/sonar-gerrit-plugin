package org.jenkinsci.plugins.sonargerrit.config;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 21:47
 *
 * <p>$Id$
 */
public interface BaseConfigTest {
  @SuppressWarnings(value = "unused")
  void testFilterConfig();

  @SuppressWarnings(value = "unused")
  void testReviewConfig();

  @SuppressWarnings(value = "unused")
  void testScoreConfig();

  @SuppressWarnings(value = "unused")
  void testNotificationConfig();

  @SuppressWarnings(value = "unused")
  void testAuthenticationConfig();

  @SuppressWarnings(value = "unused")
  void testInspectionConfig();
}
