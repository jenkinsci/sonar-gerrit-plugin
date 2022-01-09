package org.jenkinsci.plugins.sonargerrit.sonar;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 20:58
 *
 * <p>$Id$
 */
public class CommentFilterSeverityNewChangedOnlyTest extends FilterSeverityNewChangedOnlyTest {
  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getReviewConfig().getIssueFilterConfig();
  }
}
