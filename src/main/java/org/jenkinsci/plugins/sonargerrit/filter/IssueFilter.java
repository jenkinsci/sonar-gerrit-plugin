package org.jenkinsci.plugins.sonargerrit.filter;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByChangedLinesPredicate;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByFilenamesPredicate;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByMinSeverityPredicate;
import org.jenkinsci.plugins.sonargerrit.filter.predicates.ByNewPredicate;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 17:29
 * <p>
 * $Id$
 */
public class IssueFilter {
    private IssueFilterConfig filterConfig;
    private List<IssueAdapter> issues;
    private Map<String, Set<Integer>> changedLines;

    public IssueFilter(IssueFilterConfig filterConfig, List<IssueAdapter> issues, Map<String, Set<Integer>> changedLines) {
        this.filterConfig = filterConfig;
        this.issues = issues;
        this.changedLines = changedLines;
    }

    public Iterable<IssueAdapter> filter() {

        List<Predicate<IssueAdapter>> toBeApplied = new ArrayList<>();
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

        return Iterables.filter(issues, Predicates.and(toBeApplied));
    }


}
