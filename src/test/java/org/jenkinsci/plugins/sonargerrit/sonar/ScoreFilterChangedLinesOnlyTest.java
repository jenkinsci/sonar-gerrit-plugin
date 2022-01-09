package org.jenkinsci.plugins.sonargerrit.sonar;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 15:34
 *
 * <p>$Id$
 */
public class ScoreFilterChangedLinesOnlyTest extends FilterChangedLinesOnlyTest {

  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getScoreConfig().getIssueFilterConfig();
  }
}
