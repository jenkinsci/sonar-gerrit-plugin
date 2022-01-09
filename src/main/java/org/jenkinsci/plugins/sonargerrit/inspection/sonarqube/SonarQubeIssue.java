package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 29.11.2017 15:21 $Id$ */
@Restricted(NoExternalUse.class)
public class SonarQubeIssue extends Issue implements IssueAdapter {

  private String filepath;

  private final ComponentPathBuilder pathBuilder;

  private final SubJobConfig config;

  public SonarQubeIssue(Issue issue, ComponentPathBuilder pathBuilder, SubJobConfig config) {
    super(issue);
    this.pathBuilder = pathBuilder;
    this.config = config;
  }

  @Override
  public String getFilepath() {
    if (filepath == null) {
      if (pathBuilder != null) {
        filepath =
            pathBuilder
                .buildPrefixedPathForComponentWithKey(getComponent(), config.getProjectPath())
                .or(getComponent());
      } else {
        filepath = getComponent();
      }
    }
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }
}
