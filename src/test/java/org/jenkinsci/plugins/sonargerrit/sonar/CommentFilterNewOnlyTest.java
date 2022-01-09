package org.jenkinsci.plugins.sonargerrit.sonar;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 15:10 $Id$ */
public class CommentFilterNewOnlyTest extends FilterNewOnlyTest {

  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getReviewConfig().getIssueFilterConfig();
  }
}
