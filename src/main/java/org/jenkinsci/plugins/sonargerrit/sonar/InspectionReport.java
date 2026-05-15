package org.jenkinsci.plugins.sonargerrit.sonar;

import java.util.List;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.12.2017 21:55 */
@Restricted(NoExternalUse.class)
public class InspectionReport {

  private final List<Issue> issues;

  public InspectionReport(List<Issue> issues) {
    this.issues = List.copyOf(issues);
  }

  public List<Issue> getIssues() {
    return issues;
  }
}
