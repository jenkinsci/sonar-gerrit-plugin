package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import com.google.common.base.Predicate;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 17:43
 * <p>
 * $Id$
 */
public class ByFilenamesPredicate implements Predicate<IssueAdapter> {
    private final Set<String> allowedComponents;

    private ByFilenamesPredicate(Set<String> allowedComponents) {
        this.allowedComponents = new HashSet<>();
        if (allowedComponents != null) {
            this.allowedComponents.addAll(allowedComponents);
        }
    }

    @Override
    public boolean apply(IssueAdapter issue) {
        return allowedComponents != null && allowedComponents.contains(issue.getFilepath());
    }

    public static ByFilenamesPredicate apply(Set<String> allowedComponents) {
        return new ByFilenamesPredicate(allowedComponents);
    }
}
