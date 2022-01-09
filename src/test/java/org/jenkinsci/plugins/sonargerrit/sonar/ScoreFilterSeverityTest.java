package org.jenkinsci.plugins.sonargerrit.sonar;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 14.11.2017 23:35
 *
 * <p>$Id$
 */
public class ScoreFilterSeverityTest extends FilterSeverityTest {

  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getScoreConfig().getIssueFilterConfig();
  }
}
