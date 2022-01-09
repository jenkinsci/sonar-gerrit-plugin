package org.jenkinsci.plugins.sonargerrit.review;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 16:55
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public interface ConnectionInfo {

  String getChangeNumber();

  String getPatchsetNumber();
}
