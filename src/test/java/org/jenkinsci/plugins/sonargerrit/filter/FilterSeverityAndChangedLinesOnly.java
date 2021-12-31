package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.filter.util.Pair;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.review.BaseFilterTest;
import org.junit.Assert;
import org.junit.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 20:41 $Id$ */
public abstract class FilterSeverityAndChangedLinesOnly
    extends BaseFilterTest<Pair<String, Boolean>> {

  @Test
  public void testInfoSeverity() {
    doCheckSeverityAndChanged(Severity.INFO, true, 2);
    doCheckSeverityAndChanged(Severity.INFO, false, 11);
  }

  @Test
  public void testMinorSeverity() {
    doCheckSeverityAndChanged(Severity.MINOR, true, 2);
    doCheckSeverityAndChanged(Severity.MINOR, false, 10);
  }

  @Test
  public void testMajorSeverity() {
    doCheckSeverityAndChanged(Severity.MAJOR, true, 1);
    doCheckSeverityAndChanged(Severity.MAJOR, false, 7);
  }

  @Test
  public void testCriticalSeverity() {
    doCheckSeverityAndChanged(Severity.CRITICAL, true, 0);
    doCheckSeverityAndChanged(Severity.CRITICAL, false, 2);
  }

  @Test
  public void testBlockerSeverity() {
    doCheckSeverityAndChanged(Severity.BLOCKER, true, 0);
    doCheckSeverityAndChanged(Severity.BLOCKER, false, 1);
  }

  @Override
  public void setFilter(Pair<String, Boolean> severityAndChanged) {
    String severity = severityAndChanged.getFirst();
    setSeverity(getFilterConfig(), severity);
    Assert.assertEquals(severity, getFilterConfig().getSeverity());

    Boolean changedOnly = severityAndChanged.getSecond();
    setChangedOnly(getFilterConfig(), changedOnly);
    Assert.assertEquals(changedOnly, getFilterConfig().isChangedLinesOnly());
  }

  @Override
  public void resetFilter() {
    super.resetFilter();
    String severity = IssueFilterConfig.DescriptorImpl.SEVERITY;
    Boolean changedOnly = IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY;
    setFilter(new Pair<>(severity, changedOnly));
  }

  @Override
  protected void doCheckFilteredOutByCriteria(Pair<String, Boolean> severityAndChanged) {
    Severity severity = Severity.valueOf(severityAndChanged.getFirst());
    Boolean changedOnly = severityAndChanged.getSecond();

    // check that all filtered out issues have severity lower than criteria
    for (IssueAdapter issue : filteredOutIssues) {
      if (isFileChanged(issue)) {
        Assert.assertFalse(
            isSeverityCriteriaSatisfied(severity, issue)
                && isChangedLinesOnlyCriteriaSatisfied(changedOnly, issue));
      }
    }
  }

  private void doCheckSeverityAndChanged(
      Severity severity, boolean changedLinesOnly, int expectedCount) {
    Pair<String, Boolean> severityAndChanged = new Pair<>(severity.name(), changedLinesOnly);
    setFilter(severityAndChanged);
    doFilterIssues(getFilterConfig());

    doCheckCount(expectedCount);
    doCheckSeverity(severity);
    doCheckChangedLinesOnly(changedLinesOnly);
    doCheckFilteredOutByCriteria(severityAndChanged);
  }

  @Override
  protected abstract IssueFilterConfig getFilterConfig();
}
