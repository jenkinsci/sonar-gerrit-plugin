package org.jenkinsci.plugins.sonargerrit.sonar;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;
import java.io.IOException;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;

/** @author Réda Housni Alaoui */
public interface AnalysisStrategy {

  InspectionReport analyse(
      TaskListener listener,
      Revision revision,
      FilePath workspace,
      @Nullable SonarInstallation sonarQubeInstallation)
      throws IOException, InterruptedException;
}
