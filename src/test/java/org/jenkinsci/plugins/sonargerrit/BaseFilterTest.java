package org.jenkinsci.plugins.sonargerrit;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gerrit.extensions.common.DiffInfo;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.config.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.config.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.config.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 10.11.2017 21:47
 * <p/>
 * $Id$
 */
public abstract class BaseFilterTest<A> extends ReportBasedTest {
    protected Report report;
    protected SonarToGerritPublisher publisher;

    protected Set<Issue> filteredIssues;
    protected Set<Issue> filteredOutIssues;

    protected Map<String, DiffInfo> diffInfo;

    @Before
    public void initialize() throws InterruptedException, IOException, URISyntaxException {
        loadReport();
        buildPublisher();  //todo check all issues read correctly?
    }

    protected void loadReport() throws InterruptedException, IOException, URISyntaxException {
        report = readreport("filter.json");
        Assert.assertEquals(19, report.getIssues().size());
    }

    @After
    public void resetFilter() {
    }

    @After
    public void reset() {
        filteredIssues = null;
        filteredOutIssues = null;
    }

//    @Before
    public void setFilter(A a){

    }

    protected void buildPublisher() {
        publisher = new SonarToGerritPublisher();

        publisher.setSonarURL(SonarToGerritPublisher.DescriptorImpl.SONAR_URL);


//        publisher.setSubJobConfigs(SonarToGerritPublisher.DescriptorImpl.JOB_CONFIGS);
        Assert.assertEquals(1, publisher.getSubJobConfigs().size());

        publisher.setAuthConfig(null);

        publisher.setReviewConfig(new ReviewConfig());
        publisher.setNotificationConfig(new NotificationConfig());
        publisher.setScoreConfig(new ScoreConfig());
    }

    protected Multimap<String, Issue> getMultimap() {
        return SonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates("", report, report.getIssues());
    }

    protected void doFilterIssues(IssueFilterConfig config){
        // filter issues
        List<Issue> allIssues = report.getIssues();
        filteredIssues = Sets.newHashSet(publisher.filterIssuesByPredicates(allIssues, config));

        // get issues that were filtered out
        filteredOutIssues = new HashSet<>(allIssues);
        filteredOutIssues.removeAll(filteredIssues);
    }

    protected void doCheckSeverity(Severity severity) {
        // check that all remaining issues have severity higher or equal to criteria
        for (Issue issue : filteredIssues) {
            Assert.assertTrue(isSeverityCriteriaSatisfied(severity, issue));
        }
    }

    protected void doCheckNewOnly(boolean isNewOnly) {
        // check that all remaining issues are new
        for (Issue issue : filteredIssues) {
            Assert.assertTrue(isNewOnlyCriteriaSatisfied(isNewOnly, issue));
        }
    }

    protected void doCheckChangedLinesOnly(boolean isChangesLinesOnly) {
        // check that all remaining issues are in changed lines
        for (Issue issue : filteredIssues) {
            Assert.assertTrue(isChangedLinesOnlyCriteriaSatisfied(isChangesLinesOnly, issue));
        }
    }

    protected boolean isSeverityCriteriaSatisfied(Severity severity, Issue issue) {
        return issue.getSeverity().ordinal() >= severity.ordinal();
    }

    protected boolean isNewOnlyCriteriaSatisfied(Boolean isNewOnly, Issue issue) {
        return !isNewOnly || issue.isNew();
    }

    protected boolean isChangedLinesOnlyCriteriaSatisfied(Boolean isChangesLinesOnly, Issue issue) {
        return !isChangesLinesOnly || isChanged(issue.getComponent(), issue.getLine());
    }

    protected boolean isChanged(String filename, int line){
        DiffInfo diffInfo = this.diffInfo.get(filename);
        if (diffInfo == null){
            return false;
        }
        int processed = 0;
        for (DiffInfo.ContentEntry contentEntry : diffInfo.content) {
            if (contentEntry.ab != null) {
                processed += contentEntry.ab.size();
                if (processed >= line){
                    return false;
                }
            } else if (contentEntry.b != null) {
                processed += contentEntry.b.size();
                if (processed >= line){
                    return true;
                }
            }
        }
        return false;
    }

    protected abstract void doCheckFilteredOutByCriteria(A a);

    protected void doCheckCount(int expectedFilteredIssuesCount){
        // check that amount of filtered issues is equal to expected amount
        Assert.assertEquals(expectedFilteredIssuesCount, filteredIssues.size());

        // get amount of issues that are expected to be filtered out and check it
        List<Issue> allIssues = report.getIssues();
        int expectedFilteredOutCount = allIssues.size() - expectedFilteredIssuesCount;
        Assert.assertEquals(expectedFilteredOutCount, filteredOutIssues.size());
    }

    protected abstract IssueFilterConfig getFilterConfig();

    protected void setSeverity(IssueFilterConfig config, String severity) {
        config.setSeverity(severity);
        Assert.assertEquals(severity, config.getSeverity());
    }

    protected void setNewOnly(IssueFilterConfig config, Boolean newOnly) {
        config.setNewIssuesOnly(newOnly);
        Assert.assertEquals(newOnly, config.isNewIssuesOnly());
    }

    protected void setChangedOnly(IssueFilterConfig config, Boolean changedOnly) {
        config.setChangedLinesOnly(changedOnly);
        Assert.assertEquals(changedOnly, config.isChangedLinesOnly());
    }
}
