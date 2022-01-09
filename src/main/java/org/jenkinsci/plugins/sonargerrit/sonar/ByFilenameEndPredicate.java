package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.Predicate;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.12.2017 22:06 */
@Restricted(NoExternalUse.class)
public class ByFilenameEndPredicate implements Predicate<IssueAdapter> {

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
