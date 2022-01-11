package org.jenkinsci.plugins.sonargerrit.sonar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** @author Réda Housni Alaoui */
@Restricted(NoExternalUse.class)
public class Rule {

  private final String id;

  public Rule(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }

  public String createLink(String sonarQubeUrl) {
    if (sonarQubeUrl == null) {
      return id;
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
    sb.append(escapeHttp(id)); // squid%3AS1319
    return sb.toString();
  }

  private String escapeHttp(String query) {
    try {
      return URLEncoder.encode(query, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return query;
    }
  }
}
