package org.jenkinsci.plugins.sonargerrit.filter.util;

import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.11.2017 12:37
 *
 * <p>$Id$
 */
public class FileDiff {
  public String filename;
  public DiffInfo diffInfo;
}
