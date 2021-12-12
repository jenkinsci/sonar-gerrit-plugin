package org.jenkinsci.plugins.sonargerrit.inspection.entity;

import java.util.Date;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 03.12.2017 14:00 $Id$ */
public interface IssueAdapter {
  String getFilepath();

  void setFilepath(String path);

  String getKey();

  String getComponent();

  Integer getLine();

  String getMessage();

  Severity getSeverity();

  String getRule();

  String getStatus();

  boolean isNew();

  Date getCreationDate();
}
