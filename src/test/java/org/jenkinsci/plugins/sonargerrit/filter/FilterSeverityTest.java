package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.BaseFilterTest;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 14:01
 * <p/>
 * $Id$
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

    @Override
    public void setFilter(String severity) {
        setSeverity(getFilterConfig(), severity);
        Assert.assertEquals(severity, getFilterConfig().getSeverity());
    }

    @Override
    public void resetFilter() {
        super.resetFilter();
        setFilter(IssueFilterConfig.DescriptorImpl.SEVERITY);
    }

    @Override
    protected void doCheckFilteredOutByCriteria(String severityStr) {
        Severity severity = Severity.valueOf(severityStr);
        // check that all filtered out issues have severity lower than criteria
        for (Issue issue : filteredOutIssues) {
            if (isFileChanged(issue)) {
                Assert.assertFalse(isSeverityCriteriaSatisfied(severity, issue));
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

    protected abstract IssueFilterConfig getFilterConfig();


}
