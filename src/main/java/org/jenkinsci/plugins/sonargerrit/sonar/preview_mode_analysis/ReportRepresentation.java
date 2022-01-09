package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import java.util.List;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
@Restricted(NoExternalUse.class)
class ReportRepresentation {
  @SuppressWarnings(value = "unused")
  private String version;

  @SuppressWarnings(value = "unused")
  private List<IssueRepresentation> issues;

  @SuppressWarnings(value = "unused")
  private List<ComponentRepresentation> components;

  public List<IssueRepresentation> getIssues() {
    return issues;
  }

  public List<ComponentRepresentation> getComponents() {
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
