package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.review.BaseFilterTest;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 14:10
 * $Id$
 */
public abstract class FilterNewOnlyTest extends BaseFilterTest<Boolean> {
    @Test
    public void testNewOnly() {
        doCheckNewOnly(true, 2);
    }

    @Test
    public void testAll() {
        doCheckNewOnly(false, 11);
    }

    @Override
    public void setFilter(Boolean newOnly) {
        setNewOnly(getFilterConfig(), newOnly);
        Assert.assertEquals(newOnly, getFilterConfig().isNewIssuesOnly());
    }

    @Override
    public void resetFilter() {
        super.resetFilter();
        setFilter(IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY);
    }

    @Override
    protected void doCheckFilteredOutByCriteria(Boolean newOnly) {
        // check that all filtered out issues have severity lower than criteria
        for (IssueAdapter issue : filteredOutIssues) {
            if (isFileChanged(issue)) {
                Assert.assertFalse(isNewOnlyCriteriaSatisfied(newOnly, issue));
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

