package org.jenkinsci.plugins.sonargerrit.sonar;

import java.util.List;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
@Restricted(NoExternalUse.class)
class Report {
  @SuppressWarnings(value = "unused")
  private String version;

  @SuppressWarnings(value = "unused")
  private List<Issue> issues;

  @SuppressWarnings(value = "unused")
  private List<Component> components;

  public List<Issue> getIssues() {
    return issues;
  }

  public List<Component> getComponents() {
    return components;
  }

  @Override
  public String toString() {
    return "Report{"
        + "version='"
        + version
        + '\''
        + ", issues="
        + issues
        + ", components="
        + components
        + "}";
  }
}
