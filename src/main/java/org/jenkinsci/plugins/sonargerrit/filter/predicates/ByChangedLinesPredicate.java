package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import com.google.common.base.Predicate;
import com.google.common.collect.Range;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;

import java.util.*;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 20.11.2017 13:47
 * <p/>
 * $Id$
 */
public class ByChangedLinesPredicate implements Predicate<Issue> {
    private final Map<String, List<Range<Integer>>> allowedComponents;

    private ByChangedLinesPredicate(Map<String, List<Range<Integer>>> allowedComponents) {
        if (allowedComponents != null) {
            this.allowedComponents = new HashMap<>();
            this.allowedComponents.putAll(allowedComponents);
        } else {
            this.allowedComponents = null;
        }
    }

    @Override
    public boolean apply(Issue issue) {
        return allowedComponents == null
                || allowedComponents.keySet().contains(issue.getComponent())
                && isLineChanged(issue.getLine(), allowedComponents.get(issue.getComponent()));
    }

    private boolean isLineChanged(Integer line, List<Range<Integer>> changedLines){
        for (Range<Integer> r: changedLines){
            if (r.contains(line)){
                return true;
            }
            if (r.upperEndpoint().compareTo(line) > 0){
                break;
            }
        }
        return false;
    }

    public static ByChangedLinesPredicate apply(Map<String, List<Range<Integer>>> allowedComponents) {
        return new ByChangedLinesPredicate(allowedComponents);
    }
}
