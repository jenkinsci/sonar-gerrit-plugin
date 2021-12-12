package org.jenkinsci.plugins.sonargerrit.review;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 16:55
 *
 * <p>$Id$
 */
public interface ConnectionInfo {
  String getServerName();

  String getChangeNumber();

  String getPatchsetNumber();

  String getUsername();

  String getPassword();
}
