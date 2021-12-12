package org.jenkinsci.plugins.sonargerrit.review.formatter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 13:28 */
public interface TagFormatter<E extends Enum> {
  String getValueToReplace(E tag);

  String getMessage();
}
