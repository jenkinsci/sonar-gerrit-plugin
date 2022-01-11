package org.jenkinsci.plugins.sonargerrit.sonar.pull_request_analysis;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.jenkinsci.plugins.sonargerrit.sonar.AnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionReport;
import org.kohsuke.stapler.DataBoundConstructor;

/** @author Réda Housni Alaoui */
public class PullRequestAnalysisStrategy
    extends AbstractDescribableImpl<PullRequestAnalysisStrategy> implements AnalysisStrategy {

  @DataBoundConstructor
  public PullRequestAnalysisStrategy() {}

  @Override
  public InspectionReport analyse(
      Run<?, ?> run, TaskListener listener, Revision revision, FilePath workspace)
      throws InterruptedException {

    return new InspectionReport(
        PullRequestAnalysisTask.parseLastAnalysis(run).fetchIssues(listener));
  }

  @Symbol("pullRequest")
  @Extension
  public static class DescriptorImpl extends Descriptor<PullRequestAnalysisStrategy> {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Pull request analysis (since SonarQube 7.2)";
    }
  }
}
