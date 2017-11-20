package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import com.google.common.base.Predicate;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 13:24
 *
 */
public class ByNewPredicate implements Predicate<Issue> {

    private final boolean anew;

    private ByNewPredicate(boolean anew) {
        this.anew = anew;
    }

    @Override
    public boolean apply(Issue issue) {
        return !anew || issue.isNew();
    }

    public static ByNewPredicate apply(boolean anew) {
        return new ByNewPredicate(anew);
    }

}
