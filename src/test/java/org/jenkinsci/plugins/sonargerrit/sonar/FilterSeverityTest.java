package org.jenkinsci.plugins.sonargerrit.sonar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 14:01
 *
 * <p>$Id$
 */
public abstract class FilterSeverityTest extends BaseFilterTest<String> {

  @Test
  public void testInfoSeverity() {
    doCheckSeverity(Severity.INFO, 11);
  }

  @Test
  public void testMinorSeverity() {
    doCheckSeverity(Severity.MINOR, 10);
  }

  @Test
  public void testMajorSeverity() {
    doCheckSeverity(Severity.MAJOR, 7);
  }

  @Test
  public void testCriticalSeverity() {
    doCheckSeverity(Severity.CRITICAL, 2);
  }

  @Test
  public void testBlockerSeverity() {
    doCheckSeverity(Severity.BLOCKER, 1);
  }

  private void setFilter(String severity) {
    setSeverity(getFilterConfig(), severity);
    Assertions.assertEquals(severity, getFilterConfig().getSeverity());
  }

  @Override
  protected void doResetFilter() {
    super.doResetFilter();
    setFilter(IssueFilterConfig.DescriptorImpl.SEVERITY);
  }

  private void doCheckFilteredOutByCriteria(String severityStr) {
    Severity severity = Severity.valueOf(severityStr);
    // check that all filtered out issues have severity lower than criteria
    for (IssueAdapter issue : filteredOutIssues) {
      if (isFileChanged(issue)) {
        Assertions.assertFalse(isSeverityCriteriaSatisfied(severity, issue));
      }
    }
  }

  private void doCheckSeverity(Severity severity, int expectedCount) {
    setFilter(severity.name());
    doFilterIssues(getFilterConfig());

    doCheckCount(expectedCount);
    doCheckSeverity(severity);
    doCheckFilteredOutByCriteria(severity.name());
  }

  @Override
  protected abstract IssueFilterConfig getFilterConfig();
}
