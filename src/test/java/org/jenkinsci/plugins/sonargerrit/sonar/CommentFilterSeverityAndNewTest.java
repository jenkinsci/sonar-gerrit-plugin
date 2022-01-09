package org.jenkinsci.plugins.sonargerrit.sonar;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 17:19
 *
 * <p>$Id$
 */
public class CommentFilterSeverityAndNewTest extends FilterSeverityNewOnlyTest {
  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getReviewConfig().getIssueFilterConfig();
  }
}
