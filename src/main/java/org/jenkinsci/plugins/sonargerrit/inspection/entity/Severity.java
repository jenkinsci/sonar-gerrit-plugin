package org.jenkinsci.plugins.sonargerrit.inspection.entity;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
@Restricted(NoExternalUse.class)
public enum Severity {
  INFO,
  MINOR,
  MAJOR,
  CRITICAL,
  BLOCKER
}
