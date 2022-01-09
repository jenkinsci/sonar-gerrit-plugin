package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.Predicate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 20.11.2017 13:47
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public class ByChangedLinesPredicate implements Predicate<IssueAdapter> {
  private final Map<String, Set<Integer>> allowedFilesAndLines;

  private ByChangedLinesPredicate(Map<String, Set<Integer>> allowedFilesAndLines) {
    if (allowedFilesAndLines != null) {
      this.allowedFilesAndLines = new HashMap<>();
      this.allowedFilesAndLines.putAll(allowedFilesAndLines);
    } else {
      this.allowedFilesAndLines = null;
    }
  }

  @Override
  public boolean apply(IssueAdapter issue) {
    String filepath = issue.getFilepath();
    return allowedFilesAndLines == null // no filename restriction or
        || allowedFilesAndLines.containsKey(filepath) // this file was changed and
            && isLineChanged(
                issue.getLine(), allowedFilesAndLines.get(filepath)); // this line was changed
  }

  private boolean isLineChanged(Integer line, Set<Integer> changedLines) {
    return changedLines.contains(line);
  }

  public static ByChangedLinesPredicate apply(Map<String, Set<Integer>> allowedComponents) {
    return new ByChangedLinesPredicate(allowedComponents);
  }
}
