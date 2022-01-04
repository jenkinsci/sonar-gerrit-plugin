package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jenkins.tasks.SimpleBuildWrapper;

/** @author Réda Housni Alaoui */
public class EnvironmentVariableBuildWrapper extends SimpleBuildWrapper {

  private final Map<String, String> variables = new HashMap<>();

  @Override
  public void setUp(
      Context context,
      Run<?, ?> build,
      FilePath workspace,
      Launcher launcher,
      TaskListener listener,
      EnvVars initialEnvironment)
      throws IOException, InterruptedException {

    variables.forEach(context::env);
  }

  public EnvironmentVariableBuildWrapper add(String key, String value) {
    variables.put(key, value);
    return this;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<BuildWrapper> {}
}
