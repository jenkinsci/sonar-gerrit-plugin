package org.jenkinsci.plugins.sonargerrit.gerrit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueAdapter;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 12:51 */
@Restricted(NoExternalUse.class)
class CustomIssueFormatter {

  private final IssueAdapter issue;
  private final String text;
  private final String host;

  public CustomIssueFormatter(IssueAdapter issue, String text, String host) {
    this.issue = issue;
    this.host = host;
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
        return getRuleLink(issue.getRule());
      case STATUS:
        return issue.getStatus();
      case CREATION_DATE:
        return issue.getCreationDate().toString();
      default:
        throw new IllegalArgumentException("Unexpected tag " + tag);
    }
  }

  private String getRuleLink(String rule) {
    if (host != null) {
      StringBuilder sb = new StringBuilder();
      String url = host.trim();
      if (!(url.startsWith("http://") || host.startsWith("https://"))) {
        sb.append("http://");
      }
      sb.append(url);
      if (!(url.endsWith("/"))) {
        sb.append("/");
      }
      sb.append("coding_rules#rule_key=");
      sb.append(escapeHttp(rule)); // squid%3AS1319
      return sb.toString();
    }
    return rule;
  }

  private String escapeHttp(String query) {
    try {
      return URLEncoder.encode(query, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return query;
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
