package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.annotation.Nullable;
import org.jenkinsci.plugins.sonargerrit.sonar.Component;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
@Restricted(NoExternalUse.class)
class ComponentRepresentation implements Component {
  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String key;

  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String moduleKey;

  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String path;

  @SuppressWarnings("unused")
  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private String status;

  @Override
  public String getKey() {
    return key;
  }

  @Override
  @Nullable
  public String getPath() {
    return path;
  }

  @Override
  @Nullable
  public String getModuleKey() {
    return moduleKey;
  }

  @Nullable
  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "Component{"
        + "key='"
        + key
        + '\''
        + ", path='"
        + path
        + '\''
        + ", moduleKey='"
        + moduleKey
        + '\''
        + ", status='"
        + status
        + '\''
        + '}';
  }
}
