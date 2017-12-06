package org.jenkinsci.plugins.sonargerrit.config;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 21:47
 * <p>
 * $Id$
 */
public interface BaseConfigTest {
    void testFilterConfig();

    void testReviewConfig();

    void testScoreConfig();

    void testNotificationConfig();

    void testAuthenticationConfig();

    void testSonarConfig();

    void testProjectConfig();
}
