package org.jenkinsci.plugins.sonargerrit.config;

import com.google.common.base.MoreObjects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 02.12.2015 12:11 */
public class SubJobConfig extends AbstractDescribableImpl<SubJobConfig> {

  @Nonnull private String projectPath = SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH;

  @Nonnull private String sonarReportPath = SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH;

  private boolean autoMatch;

  @SuppressFBWarnings(
      value = "NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR") // values initialized by setters in
  // constructor
  public SubJobConfig(String projectPath, String sonarReportPath) {
    setProjectPath(projectPath);
    setSonarReportPath(sonarReportPath);
    setAutoMatch(InspectionConfig.DescriptorImpl.AUTO_MATCH);
  }

  @DataBoundConstructor
  public SubJobConfig() {
    this(DescriptorImpl.PROJECT_PATH, DescriptorImpl.SONAR_REPORT_PATH);
  }

  public String getProjectPath() {
    return projectPath;
  }

  public String getSonarReportPath() {
    return sonarReportPath;
  }

  public boolean isAutoMatch() {
    return autoMatch;
  }

  @DataBoundSetter
  public void setAutoMatch(boolean autoMatch) {
    this.autoMatch = autoMatch;
  }

  @DataBoundSetter
  public void setProjectPath(String projectPath) {
    this.projectPath =
        MoreObjects.firstNonNull(projectPath, SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH);
  }

  @DataBoundSetter
  public void setSonarReportPath(String sonarReportPath) {
    this.sonarReportPath =
        MoreObjects.firstNonNull(
            sonarReportPath, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return new DescriptorImpl();
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<SubJobConfig> {
    public static final String PROJECT_PATH = SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH;
    public static final String SONAR_REPORT_PATH =
        SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH;

    @Override
    public String getDisplayName() {
      return "SubJobConfig";
    }
  }
}
