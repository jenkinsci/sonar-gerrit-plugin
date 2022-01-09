package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.filter.util.Triple;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.review.BaseFilterTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 20:51 $Id$ */
public abstract class FilterSeverityNewChangedOnlyTest
    extends BaseFilterTest<Triple<String, Boolean, Boolean>> {

  @Test
  public void testInfoSeverity() {
    doCheckSeverityNewAndChanged(Severity.INFO, true, true, 1); // one new AND changed
    doCheckSeverityNewAndChanged(
        Severity.INFO, false, true, 2); // two changed AND mentioned in the report
    doCheckSeverityNewAndChanged(Severity.INFO, true, false, 2); // two new
    doCheckSeverityNewAndChanged(Severity.INFO, false, false, 11); // all but unchanged
  }

  @Test
  public void testMinorSeverity() {
    doCheckSeverityNewAndChanged(Severity.MINOR, true, true, 1);
    doCheckSeverityNewAndChanged(Severity.MINOR, false, true, 2);
    doCheckSeverityNewAndChanged(Severity.MINOR, true, false, 2);
    doCheckSeverityNewAndChanged(Severity.MINOR, false, false, 10);
  }

  @Test
  public void testMajorSeverity() {
    doCheckSeverityNewAndChanged(Severity.MAJOR, true, true, 1);
    doCheckSeverityNewAndChanged(Severity.MAJOR, false, true, 1);
    doCheckSeverityNewAndChanged(Severity.MAJOR, true, false, 2);
    doCheckSeverityNewAndChanged(Severity.MAJOR, false, false, 7);
  }

  @Test
  public void testCriticalSeverity() {
    doCheckSeverityNewAndChanged(Severity.CRITICAL, true, true, 0);
    doCheckSeverityNewAndChanged(Severity.CRITICAL, false, true, 0);
    doCheckSeverityNewAndChanged(Severity.CRITICAL, true, false, 1);
    doCheckSeverityNewAndChanged(Severity.CRITICAL, false, false, 2);
  }

  @Test
  public void testBlockerSeverity() {
    doCheckSeverityNewAndChanged(Severity.BLOCKER, true, true, 0);
    doCheckSeverityNewAndChanged(Severity.BLOCKER, false, true, 0);
    doCheckSeverityNewAndChanged(Severity.BLOCKER, true, false, 0);
    doCheckSeverityNewAndChanged(Severity.BLOCKER, false, false, 1);
  }

  private void setFilter(Triple<String, Boolean, Boolean> severityNewChanged) {
    String severity = severityNewChanged.getFirst();
    setSeverity(getFilterConfig(), severity);
    Assertions.assertEquals(severity, getFilterConfig().getSeverity());

    Boolean newOnly = severityNewChanged.getSecond();
    setNewOnly(getFilterConfig(), newOnly);
    Assertions.assertEquals(newOnly, getFilterConfig().isNewIssuesOnly());

    Boolean changedOnly = severityNewChanged.getThird();
    setChangedOnly(getFilterConfig(), changedOnly);
    Assertions.assertEquals(changedOnly, getFilterConfig().isChangedLinesOnly());
  }

  @Override
  protected void doResetFilter() {
    super.doResetFilter();
    String severity = IssueFilterConfig.DescriptorImpl.SEVERITY;
    Boolean newOnly = IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY;
    Boolean changedOnly = IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY;
    setFilter(new Triple<>(severity, newOnly, changedOnly));
  }

  private void doCheckFilteredOutByCriteria(Triple<String, Boolean, Boolean> severityNewChanged) {
    Severity severity = Severity.valueOf(severityNewChanged.getFirst());
    Boolean newOnly = severityNewChanged.getSecond();
    Boolean changedOnly = severityNewChanged.getThird();

    // check that all filtered out issues have severity lower than criteria
    for (IssueAdapter issue : filteredOutIssues) {
      if (isFileChanged(issue)) {
        Assertions.assertFalse(
            isSeverityCriteriaSatisfied(severity, issue)
                && isChangedLinesOnlyCriteriaSatisfied(changedOnly, issue)
                && isNewOnlyCriteriaSatisfied(newOnly, issue));
      }
    }
  }

  private void doCheckSeverityNewAndChanged(
      Severity severity, boolean newOnly, boolean changedLinesOnly, int expectedCount) {
    Triple<String, Boolean, Boolean> severityNewChanged =
        new Triple<>(severity.name(), newOnly, changedLinesOnly);
    setFilter(severityNewChanged);
    doFilterIssues(getFilterConfig());

    doCheckCount(expectedCount);
    doCheckSeverity(severity);
    doCheckNewOnly(newOnly);
    doCheckChangedLinesOnly(changedLinesOnly);
    doCheckFilteredOutByCriteria(severityNewChanged);
  }

  @Override
  protected abstract IssueFilterConfig getFilterConfig();
}
