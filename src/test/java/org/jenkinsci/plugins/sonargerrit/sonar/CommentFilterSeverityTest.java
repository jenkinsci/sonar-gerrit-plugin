package org.jenkinsci.plugins.sonargerrit.sonar;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 14.11.2017 23:23 $Id$ */
public class CommentFilterSeverityTest extends FilterSeverityTest {

  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getReviewConfig().getIssueFilterConfig();
  }
}
