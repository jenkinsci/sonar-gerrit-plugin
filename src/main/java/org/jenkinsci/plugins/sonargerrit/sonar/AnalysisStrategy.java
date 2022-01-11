package org.jenkinsci.plugins.sonargerrit.sonar;

import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.IOException;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;

/** @author Réda Housni Alaoui */
public interface AnalysisStrategy {

  InspectionReport analyse(
      Run<?, ?> run, TaskListener listener, Revision revision, FilePath workspace)
      throws IOException, InterruptedException;
}
