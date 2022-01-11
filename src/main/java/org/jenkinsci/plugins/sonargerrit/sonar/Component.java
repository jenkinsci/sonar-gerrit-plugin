package org.jenkinsci.plugins.sonargerrit.sonar;

import javax.annotation.Nullable;

/** @author Réda Housni Alaoui */
public interface Component {
  String getKey();

  @Nullable
  String getPath();

  @Nullable
  String getModuleKey();
}
