package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.review.BaseFilterTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 14:10 $Id$ */
public abstract class FilterNewOnlyTest extends BaseFilterTest<Boolean> {
  @Test
  public void testNewOnly() {
    doCheckNewOnly(true, 2);
  }

  @Test
  public void testAll() {
    doCheckNewOnly(false, 11);
  }

  private void setFilter(Boolean newOnly) {
    setNewOnly(getFilterConfig(), newOnly);
    Assertions.assertEquals(newOnly, getFilterConfig().isNewIssuesOnly());
  }

  @Override
  protected void doResetFilter() {
    super.doResetFilter();
    setFilter(IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY);
  }

  private void doCheckFilteredOutByCriteria(Boolean newOnly) {
    // check that all filtered out issues have severity lower than criteria
    for (IssueAdapter issue : filteredOutIssues) {
      if (isFileChanged(issue)) {
        Assertions.assertFalse(isNewOnlyCriteriaSatisfied(newOnly, issue));
      }
    }
  }

  private void doCheckNewOnly(Boolean newOnly, int expectedCount) {
    setFilter(newOnly);
    doFilterIssues(getFilterConfig());

    doCheckCount(expectedCount);
    doCheckNewOnly(newOnly);
    doCheckFilteredOutByCriteria(newOnly);
  }
}
