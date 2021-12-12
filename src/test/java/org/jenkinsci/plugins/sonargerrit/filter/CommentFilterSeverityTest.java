package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 14.11.2017 23:23 $Id$ */
public class CommentFilterSeverityTest extends FilterSeverityTest {

  @Override
  protected IssueFilterConfig getFilterConfig() {
    return publisher.getReviewConfig().getIssueFilterConfig();
  }
}
