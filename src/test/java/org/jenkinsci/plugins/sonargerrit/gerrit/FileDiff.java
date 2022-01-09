package org.jenkinsci.plugins.sonargerrit.gerrit;

import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.11.2017 12:37
 *
 * <p>$Id$
 */
public class FileDiff {
  @SuppressWarnings("unused")
  public String filename;

  @SuppressWarnings("unused")
  public DiffInfo diffInfo;
}
