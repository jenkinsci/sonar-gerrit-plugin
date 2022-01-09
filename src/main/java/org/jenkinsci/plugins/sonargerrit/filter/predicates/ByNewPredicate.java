package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import com.google.common.base.Predicate;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 13:24 */
@Restricted(NoExternalUse.class)
public class ByNewPredicate implements Predicate<IssueAdapter> {

  private final boolean anew;

  private ByNewPredicate(boolean anew) {
    this.anew = anew;
  }

  @Override
  public boolean apply(IssueAdapter issue) {
    return !anew || issue.isNew();
  }

  public static ByNewPredicate apply(boolean anew) {
    return new ByNewPredicate(anew);
  }
}
