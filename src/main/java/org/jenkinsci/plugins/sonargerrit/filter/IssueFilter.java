package org.jenkinsci.plugins.sonargerrit.filter;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByChangedLinesPredicate;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByComponentPredicate;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByMinSeverityPredicate;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByNewPredicate;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 17:29
 * <p/>
 * $Id$
 */
public class IssueFilter<I extends Issue> {
    private IssueFilterConfig filterConfig;
    private List<I> issues;
    private Map<String, List<Range<Integer>>> changedLines;

    public IssueFilter(IssueFilterConfig filterConfig, List<I> issues, Map<String, List<Range<Integer>>> changedLines) {
        this.filterConfig = filterConfig;
        this.issues = issues;
        this.changedLines = changedLines;
    }

    public Iterable<I> filter() {

        List<Predicate<Issue>> toBeApplied = new ArrayList<>();
        if (filterConfig.isChangedLinesOnly()) {
            toBeApplied.add(ByChangedLinesPredicate.apply(changedLines));
        } else {
            toBeApplied.add(ByComponentPredicate.apply(changedLines.keySet()));
        }

        if (filterConfig.isNewIssuesOnly()) {
            toBeApplied.add(ByNewPredicate.apply(filterConfig.isNewIssuesOnly()));
        }

        Severity severity = Severity.valueOf(filterConfig.getSeverity());
        if (!Severity.INFO.equals(severity)) {
            toBeApplied.add(ByMinSeverityPredicate.apply(severity));
        }

        return Iterables.filter(issues, Predicates.and(toBeApplied));
    }


}
