package com.aquarellian.sonar_gerrit.data.predicates;

import com.aquarellian.sonar_gerrit.data.entity.Issue;
import com.google.common.base.Predicate;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 13:24
 * <p/>
 * $Id$
 */
public class ByNewPredicate implements Predicate<Issue> {

    private final boolean anew;

    public static ByNewPredicate apply(boolean anew) {
        return new ByNewPredicate(anew);
    }

    private ByNewPredicate(boolean anew) {
        this.anew = anew;
    }

    @Override
    public boolean apply(Issue issue) {
        return !anew || issue.isNew();
    }

}
