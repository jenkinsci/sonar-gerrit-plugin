package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import com.google.common.base.Predicate;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 13:24
 *
 */
public class ByMinSeverityPredicate implements Predicate<Issue> {

    private final Severity severity;

    private ByMinSeverityPredicate(Severity severity) {
        this.severity = severity;
    }

    @Override
    public boolean apply(Issue issue) {
        return issue.getSeverity().equals(severity) || issue.getSeverity().ordinal() >= severity.ordinal();
    }

    public static ByMinSeverityPredicate apply(Severity severity) {
        return new ByMinSeverityPredicate(severity);
    }
}