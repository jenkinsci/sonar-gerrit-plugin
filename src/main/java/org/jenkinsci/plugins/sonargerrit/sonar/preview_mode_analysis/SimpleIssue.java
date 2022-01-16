package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.jenkinsci.plugins.sonargerrit.sonar.Components;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.Rule;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 29.11.2017 15:21 $Id$ */
@Restricted(NoExternalUse.class)
class SimpleIssue implements Issue {

  private final IssueRepresentation representation;
  private final Components components;
  private final SubJobConfig config;
  private final String sonarQubeUrl;

  private String filepath;

  public SimpleIssue(
      IssueRepresentation representation,
      Components components,
      SubJobConfig config,
      String sonarQubeUrl) {
    this.representation = representation;
    this.components = components;
    this.config = config;
    this.sonarQubeUrl = sonarQubeUrl;
  }

  @Override
  public String getRuleUrl() {
    return new Rule(getRule()).createUrl(sonarQubeUrl);
  }

  @Override
  public String inspectorName() {
    return "Sonar";
  }

  @Override
  public String inspectionId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public Optional<String> detailUrl() {
    return Optional.empty();
  }

  @Override
  public String getFilepath() {
    if (filepath == null) {
      if (components != null) {
        filepath =
            components
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

  @Override
  public String getKey() {
    return representation.getKey();
  }

  @Override
  public String getComponent() {
    return representation.getComponent();
  }

  @Override
  public Integer getLine() {
    return representation.getLine();
  }

  @Override
  public String getMessage() {
    return representation.getMessage();
  }

  @Override
  public Severity getSeverity() {
    return representation.getSeverity();
  }

  @Override
  public String getRule() {
    return representation.getRule();
  }

  @Override
  public String getStatus() {
    return representation.getStatus();
  }

  @Override
  public boolean isNew() {
    return representation.isNew();
  }

  @Override
  public Date getCreationDate() {
    return representation.getCreationDate();
  }
}
