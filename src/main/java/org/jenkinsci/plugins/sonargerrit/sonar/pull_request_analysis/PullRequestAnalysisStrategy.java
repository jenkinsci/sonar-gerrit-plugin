package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;
import java.io.IOException;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.jenkinsci.plugins.sonargerrit.sonar.AnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionReport;

/** @author Réda Housni Alaoui */
public class PullRequestAnalysisStrategy
    extends AbstractDescribableImpl<PullRequestAnalysisStrategy> implements AnalysisStrategy {
  @Override
  public InspectionReport analyse(
      TaskListener listener,
      Revision revision,
      FilePath workspace,
      @Nullable SonarInstallation sonarQubeInstallation)
      throws IOException, InterruptedException {
    return null;
  }

  @Symbol("pullRequest")
  @Extension
  public static class DescriptorImpl extends Descriptor<PullRequestAnalysisStrategy> {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Pull request analysis";
    }
  }
}
