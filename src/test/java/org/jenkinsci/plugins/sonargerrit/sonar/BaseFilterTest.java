package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 10.11.2017 21:47
 *
 * <p>$Id$
 */
@EnableJenkinsRule
public abstract class BaseFilterTest<A> {
  protected Report report;
  protected SonarToGerritPublisher publisher;

  protected Set<IssueAdapter> filteredIssues;
  protected Set<IssueAdapter> filteredOutIssues;

  protected Map<String, DiffInfo> diffInfo;

  @BeforeEach
  public final void initialize() throws InterruptedException, IOException, URISyntaxException {
    loadReport();
    buildPublisher(); // todo check all issues read correctly?
    diffInfo = JsonReports.readChange("diff_info.json");
  }

  protected void loadReport() throws InterruptedException, IOException, URISyntaxException {
    report = JsonReports.readReport("filter.json");
    Assertions.assertEquals(19, report.getIssues().size());
  }

  @AfterEach
  public final void resetFilter() {
    doResetFilter();
  }

  protected void doResetFilter() {}

  @AfterEach
  public final void reset() {
    filteredIssues = null;
    filteredOutIssues = null;
    diffInfo = null;
  }

  protected void buildPublisher() {
    publisher = new SonarToGerritPublisher();

    publisher.setSonarURL(SonarToGerritPublisher.DescriptorImpl.SONAR_URL);

    publisher.setAuthConfig(null);

    publisher.setReviewConfig(new ReviewConfig());
    publisher.setNotificationConfig(new NotificationConfig());
    publisher.setScoreConfig(new ScoreConfig());
  }

  protected void doFilterIssues(IssueFilterConfig config) {
    // filter issues
    List<Issue> allIssues = report.getIssues();
    List<IssueAdapter> allIssuesAdp = new ArrayList<>();
    for (Issue i : allIssues) {
      allIssuesAdp.add(new SonarQubeIssue(i, null, new SubJobConfig()));
    }

    // todo temporary - should be realized in publisher
    // todo check filtered out as unchanged file
    //        List<Issue> step2 = allIssues;
    Map<String, Set<Integer>> changed = getChangedLines();

    IssueFilter filter = new IssueFilter(config, allIssuesAdp, changed);
    filteredIssues = Sets.newHashSet(filter.filter());

    // get issues that were filtered out
    filteredOutIssues = new HashSet<>(allIssuesAdp);
    filteredOutIssues.removeAll(filteredIssues);
  }

  protected Map<String, Set<Integer>> getChangedLines() {
    Map<String, Set<Integer>> changed = new HashMap<>();
    if (diffInfo != null) {
      for (String s : diffInfo.keySet()) {
        changed.put(s, toChangedLines(diffInfo.get(s)));
      }
    }
    return changed;
  }

  private Set<Integer> toChangedLines(DiffInfo diffInfo) {
    Set<Integer> rangeSet = new HashSet<>();
    int processed = 0;
    for (DiffInfo.ContentEntry contentEntry : diffInfo.content) {
      if (contentEntry.ab != null) {
        processed += contentEntry.ab.size();
      } else if (contentEntry.b != null) {
        int start = processed + 1;
        int end = processed + contentEntry.b.size();
        for (int i = start; i <= end; i++) {
          rangeSet.add(i);
        }
        processed = end;
      }
    }
    return rangeSet;
  }

  protected void doCheckSeverity(Severity severity) {
    // check that all remaining issues have severity higher or equal to criteria
    for (IssueAdapter issue : filteredIssues) {
      Assertions.assertTrue(isSeverityCriteriaSatisfied(severity, issue));
    }
  }

  protected void doCheckNewOnly(boolean isNewOnly) {
    // check that all remaining issues are new
    for (IssueAdapter issue : filteredIssues) {
      Assertions.assertTrue(isNewOnlyCriteriaSatisfied(isNewOnly, issue));
    }
  }

  protected void doCheckChangedLinesOnly(boolean isChangesLinesOnly) {
    // check that all remaining issues are in changed lines
    for (IssueAdapter issue : filteredIssues) {
      Assertions.assertTrue(isChangedLinesOnlyCriteriaSatisfied(isChangesLinesOnly, issue));
    }
  }

  protected boolean isSeverityCriteriaSatisfied(Severity severity, IssueAdapter issue) {
    return issue.getSeverity().ordinal() >= severity.ordinal();
  }

  protected boolean isNewOnlyCriteriaSatisfied(Boolean isNewOnly, IssueAdapter issue) {
    return !isNewOnly || issue.isNew();
  }

  protected boolean isChangedLinesOnlyCriteriaSatisfied(
      Boolean isChangesLinesOnly, IssueAdapter issue) {
    return !isChangesLinesOnly || isChanged(issue.getFilepath(), issue.getLine());
  }

  protected boolean isFileChanged(IssueAdapter issue) {
    String filename = issue.getFilepath();
    DiffInfo diffInfo = this.diffInfo.get(filename);
    return diffInfo != null;
  }

  protected boolean isChanged(String filename, int line) {
    DiffInfo diffInfo = this.diffInfo.get(filename);
    if (diffInfo == null) {
      return false;
    }
    int processed = 0;
    for (DiffInfo.ContentEntry contentEntry : diffInfo.content) {
      if (contentEntry.ab != null) {
        processed += contentEntry.ab.size();
        if (processed >= line) {
          return false;
        }
      } else if (contentEntry.b != null) {
        processed += contentEntry.b.size();
        if (processed >= line) {
          return true;
        }
      }
    }
    return false;
  }

  protected void doCheckCount(int expectedFilteredIssuesCount) {
    // check that amount of filtered issues is equal to expected amount
    Assertions.assertEquals(expectedFilteredIssuesCount, filteredIssues.size());

    // get amount of issues that are expected to be filtered out and check it
    List<Issue> allIssues = report.getIssues();
    int expectedFilteredOutCount = allIssues.size() - expectedFilteredIssuesCount;
    Assertions.assertEquals(expectedFilteredOutCount, filteredOutIssues.size());
  }

  protected abstract IssueFilterConfig getFilterConfig();

  protected void setSeverity(IssueFilterConfig config, String severity) {
    config.setSeverity(severity);
    Assertions.assertEquals(severity, config.getSeverity());
  }

  protected void setNewOnly(IssueFilterConfig config, Boolean newOnly) {
    config.setNewIssuesOnly(newOnly);
    Assertions.assertEquals(newOnly, config.isNewIssuesOnly());
  }

  protected void setChangedOnly(IssueFilterConfig config, Boolean changedOnly) {
    config.setChangedLinesOnly(changedOnly);
    Assertions.assertEquals(changedOnly, config.isChangedLinesOnly());
  }
}
