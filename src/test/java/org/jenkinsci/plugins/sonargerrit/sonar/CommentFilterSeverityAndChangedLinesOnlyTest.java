package org.jenkinsci.plugins.sonargerrit.sonar;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 20:48 $Id$ */
public class CommentFilterSeverityAndChangedLinesOnlyTest
    extends FilterSeverityAndChangedLinesOnlyTest {

  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getReviewConfig().getIssueFilterConfig();
  }
}
