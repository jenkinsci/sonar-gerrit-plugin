package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import com.google.common.base.Predicate;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 17:43
 * <p/>
 * $Id$
 */
public class ByComponentPredicate implements Predicate<Issue> {
    private final Set<String> allowedComponents;

    private ByComponentPredicate(Set<String> allowedComponents) {
        this.allowedComponents = new HashSet<>();
        if (allowedComponents != null) {
            this.allowedComponents.addAll(allowedComponents);
        }
    }

    @Override
    public boolean apply(Issue issue) {
        return allowedComponents != null && allowedComponents.contains(issue.getComponent());
    }

    public static ByComponentPredicate apply(Set<String> allowedComponents) {
        return new ByComponentPredicate(allowedComponents);
    }
}
