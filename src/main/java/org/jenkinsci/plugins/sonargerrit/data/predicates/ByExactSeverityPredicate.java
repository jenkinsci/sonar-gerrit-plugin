package org.jenkinsci.plugins.sonargerrit.data.predicates;

import org.jenkinsci.plugins.sonargerrit.data.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.data.entity.Severity;
import com.google.common.base.Predicate;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 13:25
 *
 */
public class ByExactSeverityPredicate implements Predicate<Issue> {

    private final Severity severity;

    private ByExactSeverityPredicate(Severity severity) {
        this.severity = severity;
    }

    @Override
    public boolean apply(Issue issue) {
        return issue.getSeverity().equals(severity);
    }

    public static ByExactSeverityPredicate apply(Severity severity) {
        return new ByExactSeverityPredicate(severity);
    }

}

