package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.BaseFilterTest;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.filter.util.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 20:51
 * $Id$
 */
public abstract class FilterSeverityNewChangedOnly extends BaseFilterTest<Triple<String, Boolean, Boolean>> {

        @Test
        public void testInfoSeverity() {
            doCheckSeverityAndChanged(Severity.INFO, true, true, 19);
            doCheckSeverityAndChanged(Severity.INFO, false, true, 19);
            doCheckSeverityAndChanged(Severity.INFO, true, false, 19);
            doCheckSeverityAndChanged(Severity.INFO, false, false, 19);
        }

        @Test
        public void testMinorSeverity() {
            doCheckSeverityAndChanged(Severity.MINOR, true, true, 19);
            doCheckSeverityAndChanged(Severity.MINOR, false, true, 19);
            doCheckSeverityAndChanged(Severity.MINOR, true, false, 19);
            doCheckSeverityAndChanged(Severity.MINOR, false, false, 19);
        }

        @Test
        public void testMajorSeverity() {
            doCheckSeverityAndChanged(Severity.MAJOR, true, true, 19);
            doCheckSeverityAndChanged(Severity.MAJOR, false, true, 19);
            doCheckSeverityAndChanged(Severity.MAJOR, true, false, 19);
            doCheckSeverityAndChanged(Severity.MAJOR, false, false, 19);
        }

        @Test
        public void testCriticalSeverity() {
            doCheckSeverityAndChanged(Severity.CRITICAL, true, true, 19);
            doCheckSeverityAndChanged(Severity.CRITICAL, false, true, 19);
            doCheckSeverityAndChanged(Severity.CRITICAL, true, false, 19);
            doCheckSeverityAndChanged(Severity.CRITICAL, false, false, 19);
        }

        @Test
        public void testBlockerSeverity() {
            doCheckSeverityAndChanged(Severity.BLOCKER, true, true, 19);
            doCheckSeverityAndChanged(Severity.BLOCKER, false, true, 19);
            doCheckSeverityAndChanged(Severity.BLOCKER, true, false, 19);
            doCheckSeverityAndChanged(Severity.BLOCKER, false, false, 19);
        }

    @Override
    public void initialize() throws InterruptedException, IOException, URISyntaxException {
        super.initialize();
        diffInfo = readChange("diff_info.json");
    }

    @Override
    public void reset() {
        super.reset();
        diffInfo = null;
    }

    @Override
        public void setFilter(Triple<String, Boolean, Boolean> severityNewChanged) {
            String severity = severityNewChanged.getFirst();
            setSeverity(getFilterConfig(), severity);
            Assert.assertEquals(severity, getFilterConfig().getSeverity());

            Boolean newOnly = severityNewChanged.getSecond();
            setNewOnly(getFilterConfig(), newOnly);
            Assert.assertEquals(newOnly, getFilterConfig().isNewIssuesOnly());

            Boolean changedOnly = severityNewChanged.getThird();
            setChangedOnly(getFilterConfig(), changedOnly);
            Assert.assertEquals(changedOnly, getFilterConfig().isChangedLinesOnly());
        }

        @Override
        public void resetFilter() {
            super.resetFilter();
            String severity = IssueFilterConfig.DescriptorImpl.SEVERITY;
            Boolean newOnly = IssueFilterConfig.DescriptorImpl.NEW_ISSUES_ONLY;
            Boolean changedOnly = IssueFilterConfig.DescriptorImpl.CHANGED_LINES_ONLY;
            setFilter(new Triple<String, Boolean, Boolean>(severity, newOnly, changedOnly));
        }

        @Override
        protected void doCheckFilteredOutByCriteria(Triple<String, Boolean, Boolean> severityNewChanged) {
            Severity severity = Severity.valueOf(severityNewChanged.getFirst());
            Boolean newOnly = severityNewChanged.getSecond();
            Boolean changedOnly = severityNewChanged.getThird();

            // check that all filtered out issues have severity lower than criteria
            for (Issue issue : filteredOutIssues) {
                Assert.assertFalse(isSeverityCriteriaSatisfied(severity, issue)
                        && isChangedLinesOnlyCriteriaSatisfied(changedOnly, issue)
                        && isNewOnlyCriteriaSatisfied(newOnly, issue));
            }
        }

    private void doCheckSeverityAndChanged(Severity severity, boolean newOnly, boolean changedLinesOnly, int expectedCount) {
        Triple<String, Boolean, Boolean> severityNewChanged = new Triple<String, Boolean, Boolean>(severity.name(), newOnly, changedLinesOnly);
        setFilter(severityNewChanged);
        doFilterIssues(getFilterConfig());

        // todo dummy
//        this.filteredIssues = new HashSet<>();
//        for (Issue issue : report.getIssues()) {
//            if (issue.getComponent().equals("com.aquarellian:sonar-gerrit:src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java") && isSeverityCriteriaSatisfied(severity, issue) && isNewOnlyCriteriaSatisfied(newOnly, issue))
//                this.filteredIssues.add(issue);
//        }

        doCheckCount(expectedCount);
        doCheckSeverity(severity);
        doCheckNewOnly(newOnly);
        doCheckChangedLinesOnly(changedLinesOnly);
        doCheckFilteredOutByCriteria(severityNewChanged);
    }

    protected abstract IssueFilterConfig getFilterConfig();

}
