package org.jenkinsci.plugins.sonargerrit.filter.predicates;

import com.google.common.base.Predicate;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 13:25 */
public class ByExactSeverityPredicate implements Predicate<IssueAdapter> {

  private final Severity severity;

  private ByExactSeverityPredicate(Severity severity) {
    this.severity = severity;
  }

  @Override
  public boolean apply(IssueAdapter issue) {
    return issue.getSeverity().equals(severity);
  }

  public static ByExactSeverityPredicate apply(Severity severity) {
    return new ByExactSeverityPredicate(severity);
  }
}
