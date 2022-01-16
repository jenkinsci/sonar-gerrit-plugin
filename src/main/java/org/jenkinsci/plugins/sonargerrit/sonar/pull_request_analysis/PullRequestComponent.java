package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import javax.annotation.Nullable;
import me.redaalaoui.org.sonarqube.ws.Issues;
import org.jenkinsci.plugins.sonargerrit.sonar.Component;

/** @author Réda Housni Alaoui */
class PullRequestComponent implements Component {

  private final Issues.Component component;

  public PullRequestComponent(Issues.Component component) {
    this.component = component;
  }

  @Override
  public String getKey() {
    return component.getKey();
  }

  @Nullable
  @Override
  public String getPath() {
    return component.getPath();
  }

  @Nullable
  @Override
  public String getModuleKey() {
    return null;
  }
}
