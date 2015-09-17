package com.aquarellian.plugins.jenkins.sonargerrit.data.predicates;

import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Issue;
import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Severity;
import com.google.common.base.Predicate;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 13:25
 * <p/>
 * $Id$
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

