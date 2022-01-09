package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.Predicate;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 13:25 */
@Restricted(NoExternalUse.class)
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
