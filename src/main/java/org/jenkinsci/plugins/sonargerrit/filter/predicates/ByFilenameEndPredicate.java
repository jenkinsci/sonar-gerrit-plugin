package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import com.google.common.base.Predicate;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 19.12.2017 22:06
 */
public class ByFilenameEndPredicate  implements Predicate<IssueAdapter> {

    private final String filename;

    private ByFilenameEndPredicate(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean apply(IssueAdapter issue) {
        return filename.endsWith(issue.getFilepath());
    }

    public static ByFilenameEndPredicate apply(String filename) {
        return new ByFilenameEndPredicate(filename);
    }
}
