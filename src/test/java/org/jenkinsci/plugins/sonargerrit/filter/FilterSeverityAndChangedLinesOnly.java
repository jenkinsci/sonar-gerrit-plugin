package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.BaseFilterTest;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.data.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.data.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.filter.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 20:41
 * $Id$
 */
public abstract class FilterSeverityAndChangedLinesOnly  extends BaseFilterTest<Pair<String, Boolean>> {

    @Test
    public void testInfoSeverity() {
        doCheckSeverityAndChanged(Severity.INFO, true, 1);
        doCheckSeverityAndChanged(Severity.INFO, false, 19);
    }

    @Test
    public void testMinorSeverity() {
        doCheckSeverityAndChanged(Severity.MINOR, true, 1);
        doCheckSeverityAndChanged(Severity.MINOR, false, 19);
    }

    @Test
    public void testMajorSeverity() {
        doCheckSeverityAndChanged(Severity.MAJOR, true, 1);
        doCheckSeverityAndChanged(Severity.MAJOR, false, 12);
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
    public void initialize() throws InterruptedException, IOException, URISyntaxException {
        super.initialize();
        diffInfo = readChange("diff_info.json");
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
        setFilter(new Pair<String, Boolean>(severity, changedOnly));
    }

    @Override
    public void reset() {
        super.reset();
        diffInfo = null;
    }

    @Override
    protected void doCheckFilteredOutByCriteria(Pair<String, Boolean> severityAndChanged) {
        Severity severity = Severity.valueOf(severityAndChanged.getFirst());
        Boolean changedOnly = severityAndChanged.getSecond();

        // check that all filtered out issues have severity lower than criteria
        for (Issue issue : filteredOutIssues) {
            Assert.assertFalse(isSeverityCriteriaSatisfied(severity, issue) && isChangedLinesOnlyCriteriaSatisfied(changedOnly, issue));
        }
    }

    private void doCheckSeverityAndChanged(Severity severity, boolean changedLinesOnly, int expectedCount) {
        Pair<String, Boolean> severityAndChanged = new Pair<String, Boolean>(severity.name(), changedLinesOnly);
        setFilter(severityAndChanged);
        doFilterIssues(getFilterConfig());

//        // todo dummy
//        this.filteredIssues = new HashSet<>();
//        for (Issue issue : report.getIssues()) {
//            if (issue.getComponent().equals("com.aquarellian:sonar-gerrit:src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java") && isSeverityCriteriaSatisfied(severity, issue))
//                this.filteredIssues.add(issue);
//        }


        doCheckCount(expectedCount);
        doCheckSeverity(severity);
        doCheckChangedLinesOnly(changedLinesOnly);
        doCheckFilteredOutByCriteria(severityAndChanged);
    }

    protected abstract IssueFilterConfig getFilterConfig();


}
