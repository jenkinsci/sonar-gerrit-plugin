package org.jenkinsci.plugins.sonargerrit.inspection.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
@Restricted(NoExternalUse.class)
public class Rule {

  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String key;

  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String rule;

  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String repository;

  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String name;

  public String getKey() {
    return key;
  }

  public String getRule() {
    return rule;
  }

  public String getRepository() {
    return repository;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Rule{"
        + "key='"
        + key
        + '\''
        + ", rule='"
        + rule
        + '\''
        + ", repository='"
        + repository
        + '\''
        + ", name='"
        + name
        + '\''
        + '}';
  }
}
