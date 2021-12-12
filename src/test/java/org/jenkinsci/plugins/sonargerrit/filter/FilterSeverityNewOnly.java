package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.filter.util.Pair;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.review.BaseFilterTest;
import org.junit.Assert;
import org.junit.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 16:33 $Id$ */
public abstract class FilterSeverityNewOnly extends BaseFilterTest<Pair<String, Boolean>> {

  @Test
  public void testInfoSeverity() {
    doCheckSeverityNew(Severity.INFO, true, 2);
    doCheckSeverityNew(Severity.INFO, false, 11);
  }

  @Test
  public void testMinorSeverity() {
    doCheckSeverityNew(Severity.MINOR, true, 2);
    doCheckSeverityNew(Severity.MINOR, false, 10);
  }

  @Test
  public void testMajorSeverity() {
    doCheckSeverityNew(Severity.MAJOR, true, 2);
    doCheckSeverityNew(Severity.MAJOR, false, 7);
  }

  @Test
  public void testCriticalSeverity() {
    doCheckSeverityNew(Severity.CRITICAL, true, 1);
    doCheckSeverityNew(Severity.CRITICAL, false, 2);
  }

  @Test
  public void testBlockerSeverity() {
    doCheckSeverityNew(Severity.BLOCKER, true, 0);
    doCheckSeverityNew(Severity.BLOCKER, false, 1);
  }

  @Override
  public void setFilter(Pair<String, Boolean> severityAndNew) {
    String severity = severityAndNew.getFirst();
    setSeverity(getFilterConfig(), severity);
    Assert.assertEquals(severity, getFilterConfig().getSeverity());

    Boolean newOnly = severityAndNew.getSecond();
    setNewOnly(getFilterConfig(), newOnly);
    Assert.assertEquals(newOnly, getFilterConfig().isNewIssuesOnly());
  }

  @Override
  public void resetFilter() {
    super.resetFilter();
    String severity = IssueFilterConfig.DescriptorImpl.SEVERITY;
    Boolean newOnly = IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY;
    setFilter(new Pair<String, Boolean>(severity, newOnly));
  }

  @Override
  protected void doCheckFilteredOutByCriteria(Pair<String, Boolean> severityAndNew) {
    Severity severity = Severity.valueOf(severityAndNew.getFirst());
    Boolean newOnly = severityAndNew.getSecond();

    // check that all filtered out issues have severity lower than criteria
    for (IssueAdapter issue : filteredOutIssues) {
      if (isFileChanged(issue)) {
        Assert.assertFalse(
            isSeverityCriteriaSatisfied(severity, issue)
                && isNewOnlyCriteriaSatisfied(newOnly, issue));
      }
    }
  }

  private void doCheckSeverityNew(Severity severity, boolean newOnly, int expectedCount) {
    Pair<String, Boolean> severityAndNew = new Pair<String, Boolean>(severity.name(), newOnly);
    setFilter(severityAndNew);
    doFilterIssues(getFilterConfig());

    doCheckCount(expectedCount);
    doCheckSeverity(severity);
    doCheckNewOnly(newOnly);
    doCheckFilteredOutByCriteria(severityAndNew);
  }

  protected abstract IssueFilterConfig getFilterConfig();
}
