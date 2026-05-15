package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import java.util.Date;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
@Restricted(NoExternalUse.class)
final class IssueRepresentation {

  @SuppressWarnings("unused")
  private String key;

  @SuppressWarnings("unused")
  private String component;

  @SuppressWarnings("unused")
  private Integer line;

  @SuppressWarnings("unused")
  private String message;

  @SuppressWarnings("unused")
  private Severity severity;

  @SuppressWarnings("unused")
  private String rule;

  @SuppressWarnings("unused")
  private String status;

  @SuppressWarnings("unused")
  private Boolean isNew;

  @SuppressWarnings("unused")
  private Date creationDate;

  @SuppressWarnings("unused")
  public IssueRepresentation() {}

  public String getKey() {
    return key;
  }

  public String getComponent() {
    return component;
  }

  public Integer getLine() {
    return line;
  }

  public String getMessage() {
    return message;
  }

  public Severity getSeverity() {
    return severity;
  }

  public String getRule() {
    return rule;
  }

  public String getStatus() {
    return status;
  }

  public boolean isNew() {
    return isNew != null && isNew;
  }

  public Date getCreationDate() {
    return new Date(creationDate.getTime());
  }

  @Override
  public String toString() {
    return "Issue{"
        + "key='"
        + key
        + '\''
        + ", component='"
        + component
        + '\''
        + ", line="
        + line
        + ", message='"
        + message
        + '\''
        + ", severity='"
        + severity
        + '\''
        + ", rule='"
        + rule
        + '\''
        + ", status='"
        + status
        + '\''
        + ", isNew="
        + isNew
        + '}';
  }
}
