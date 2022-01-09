package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 29.11.2017 15:21 $Id$ */
@Restricted(NoExternalUse.class)
class SimpleIssue implements Issue {

  private final IssueRepresentation representation;
  private final ComponentPathBuilder pathBuilder;
  private final SubJobConfig config;
  private final String sonarQubeUrl;

  private String filepath;

  public SimpleIssue(
      IssueRepresentation representation,
      ComponentPathBuilder pathBuilder,
      SubJobConfig config,
      String sonarQubeUrl) {
    this.representation = representation;
    this.pathBuilder = pathBuilder;
    this.config = config;
    this.sonarQubeUrl = sonarQubeUrl;
  }

  @Override
  public String getRuleLink() {
    if (sonarQubeUrl == null) {
      return getRule();
    }
    StringBuilder sb = new StringBuilder();
    String url = sonarQubeUrl.trim();
    if (!(url.startsWith("http://") || sonarQubeUrl.startsWith("https://"))) {
      sb.append("http://");
    }
    sb.append(url);
    if (!(url.endsWith("/"))) {
      sb.append("/");
    }
    sb.append("coding_rules#rule_key=");
    sb.append(escapeHttp(getRule())); // squid%3AS1319
    return sb.toString();
  }

  private String escapeHttp(String query) {
    try {
      return URLEncoder.encode(query, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return query;
    }
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
