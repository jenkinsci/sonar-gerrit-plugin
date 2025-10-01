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
      return switch (tag) {
          case KEY -> issue.getKey();
          case COMPONENT -> issue.getComponent();
          case MESSAGE -> issue.getMessage();
          case SEVERITY -> issue.getSeverity().name();
          case RULE -> issue.getRule();
          case RULE_URL -> issue.getRuleUrl();
          case STATUS -> issue.getStatus();
          case CREATION_DATE -> issue.getCreationDate().toString();
      };
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
