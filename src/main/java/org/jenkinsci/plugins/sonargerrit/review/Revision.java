package org.jenkinsci.plugins.sonargerrit.review;

import java.util.Set;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.12.2017 21:58 */
@Restricted(NoExternalUse.class)
public interface Revision {

  Set<String> getChangedFiles();
}
