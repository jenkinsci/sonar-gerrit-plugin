package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.BaseFilterTest;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 15:28
 * $Id$
 */
public abstract class FilterChangedLinesOnlyTest extends BaseFilterTest<Boolean> {
    @Override
    public void initialize() throws InterruptedException, IOException, URISyntaxException {
        super.initialize();
        diffInfo = readChange("diff_info.json");
    }

    @Test
    public void testChangedLinesOnly() {
        doCheckChangedLinesOnly(true, 1);
    }

    @Test
    public void testAll() {
        doCheckChangedLinesOnly(false, 19);
    }

    @Override
    public void setFilter(Boolean changedOnly) {
        setChangedOnly(getFilterConfig(), changedOnly);
        Assert.assertEquals(changedOnly, getFilterConfig().isChangedLinesOnly());
    }

    @Override
    public void resetFilter() {
        super.resetFilter();
        setFilter(IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY);
    }

    @Override
    public void reset() {
        super.reset();
        diffInfo = null;
    }

    @Override
    protected void doCheckFilteredOutByCriteria(Boolean changedOnly) {
        // check that all filtered out issues have severity lower than criteria
        for (Issue issue : filteredOutIssues) {
            Assert.assertFalse(isChangedLinesOnlyCriteriaSatisfied(changedOnly, issue));
        }
    }

    private void doCheckChangedLinesOnly(Boolean changedOnly, int expectedCount) {
        setFilter(changedOnly);
        doFilterIssues(getFilterConfig());

        //dummy
//        this.filteredIssues = new HashSet<>();
//        for (Issue issue : report.getIssues()) {
//            if (issue.getComponent().equals("com.aquarellian:sonar-gerrit:src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java"))
//                this.filteredIssues.add(issue);
//        }

        doCheckCount(expectedCount);
        doCheckChangedLinesOnly(changedOnly);
        doCheckFilteredOutByCriteria(changedOnly);
    }

}

