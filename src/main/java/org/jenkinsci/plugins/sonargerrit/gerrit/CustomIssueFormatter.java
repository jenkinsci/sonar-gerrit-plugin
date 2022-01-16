package org.jenkinsci.plugins.sonargerrit.gerrit;

import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 12:51 */
@Restricted(NoExternalUse.class)
class CustomIssueFormatter {

  private final Issue issue;
  private final String text;

  public CustomIssueFormatter(Issue issue, String text) {
    this.issue = issue;
    this.text = text;
  }

  public String getMessage() {
    String res = text;
    for (Tag tag : Tag.values()) {
      if (res.contains(tag.getName())) {
        res = res.replace(tag.getName(), getValueToReplace(tag));
      }
    }
    return res;
  }

  private String getValueToReplace(Tag tag) {
    switch (tag) {
      case KEY:
        return issue.getKey();
      case COMPONENT:
        return issue.getComponent();
      case MESSAGE:
        return issue.getMessage();
      case SEVERITY:
        return issue.getSeverity().name();
      case RULE:
        return issue.getRule();
      case RULE_URL:
        return issue.getRuleUrl();
      case STATUS:
        return issue.getStatus();
      case CREATION_DATE:
        return issue.getCreationDate().toString();
      default:
        throw new IllegalArgumentException("Unexpected tag " + tag);
    }
  }

  public enum Tag {
    KEY("<key>"),
    COMPONENT("<component>"),
    MESSAGE("<message>"),
    SEVERITY("<severity>"),
    RULE("<rule>"),
    RULE_URL("<rule_url>"),
    STATUS("<status>"),
    CREATION_DATE("<creation_date>");

    private final String name;

    Tag(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
