package org.jenkinsci.plugins.sonargerrit.config;

import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 21:47
 *
 * <p>$Id$
 */
@EnableJenkinsRule
public abstract class BaseConfigTest {

  @Test
  public final void testFilterConfig() {
    doTestFilterConfig();
  }

  protected abstract void doTestFilterConfig();

  @Test
  public final void testReviewConfig() {
    doTestReviewConfig();
  }

  protected abstract void doTestReviewConfig();

  @Test
  public final void testScoreConfig() {
    doTestScoreConfig();
  }

  protected abstract void doTestScoreConfig();

  @Test
  public final void testNotificationConfig() {
    doTestNotificationConfig();
  }

  protected abstract void doTestNotificationConfig();

  @Test
  public final void testAuthenticationConfig() {
    doTestAuthenticationConfig();
  }

  protected abstract void doTestAuthenticationConfig();

  @Test
  public final void testInspectionConfig() {
    doTestInspectionConfig();
  }

  protected abstract void doTestInspectionConfig();
}
