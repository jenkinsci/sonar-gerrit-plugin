package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 17:29
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public class IssueFilter {
  private final IssueFilterConfig filterConfig;
  private final List<Issue> issues;
  private final Map<String, Set<Integer>> changedLines;

  public IssueFilter(
      IssueFilterConfig filterConfig, List<Issue> issues, Map<String, Set<Integer>> changedLines) {
    this.filterConfig = filterConfig;
    this.issues = issues;
    this.changedLines = changedLines;
  }

  public Iterable<Issue> filter() {

    List<Predicate<Issue>> toBeApplied = new ArrayList<>();
    if (filterConfig.isChangedLinesOnly()) {
      toBeApplied.add(ByChangedLinesPredicate.apply(changedLines));
    } else {
      toBeApplied.add(ByFilenamesPredicate.apply(changedLines.keySet()));
    }

    if (filterConfig.isNewIssuesOnly()) {
      toBeApplied.add(ByNewPredicate.apply(filterConfig.isNewIssuesOnly()));
    }

    Severity severity = Severity.valueOf(filterConfig.getSeverity());
    if (!Severity.INFO.equals(severity)) {
      toBeApplied.add(ByMinSeverityPredicate.apply(severity));
    }

    String includedPathsGlobPattern = filterConfig.getIncludedPathsGlobPattern();
    if (includedPathsGlobPattern != null && !includedPathsGlobPattern.isEmpty()) {
      toBeApplied.add(new ByGlobPatternPredicate(includedPathsGlobPattern));
    }

    String excludedPathsGlobPattern = filterConfig.getExcludedPathsGlobPattern();
    if (excludedPathsGlobPattern != null && !excludedPathsGlobPattern.isEmpty()) {
      toBeApplied.add(new ByGlobPatternPredicate(excludedPathsGlobPattern).negate());
    }

    return Iterables.filter(issues, Predicates.and(toBeApplied));
  }
}
