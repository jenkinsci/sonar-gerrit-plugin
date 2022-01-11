package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.collect.ImmutableList;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.PreviewModeAnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SonarQubeInstallations;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 07.12.2017 13:45 $Id$ */
public class Inspection extends AbstractDescribableImpl<Inspection> {

  // Only kept for backward compatibility purpose
  private transient String serverURL;

  private transient String sonarQubeInstallationName;

  private transient SubJobConfig baseConfig;

  private transient Collection<SubJobConfig> subJobConfigs;

  private transient String type;

  private AnalysisStrategy analysisStrategy = new PreviewModeAnalysisStrategy();

  @SuppressWarnings("unused")
  protected Object readResolve() {
    if (baseConfig == null
        && subJobConfigs == null
        && type == null
        && serverURL == null
        && sonarQubeInstallationName == null) {
      return this;
    }

    PreviewModeAnalysisStrategy previewModeAnalysisStrategy = new PreviewModeAnalysisStrategy();
    analysisStrategy = previewModeAnalysisStrategy;
    Optional.ofNullable(baseConfig).ifPresent(previewModeAnalysisStrategy::setBaseConfig);
    Optional.ofNullable(subJobConfigs).ifPresent(previewModeAnalysisStrategy::setSubJobConfigs);
    Optional.ofNullable(type).ifPresent(previewModeAnalysisStrategy::setType);
    Optional.ofNullable(serverURL)
        .map(url -> SonarQubeInstallations.get().findOrCreate(serverURL).getName())
        .ifPresent(previewModeAnalysisStrategy::setSonarQubeInstallationName);
    Optional.ofNullable(sonarQubeInstallationName)
        .ifPresent(previewModeAnalysisStrategy::setSonarQubeInstallationName);

    baseConfig = null;
    subJobConfigs = null;
    type = null;
    serverURL = null;
    sonarQubeInstallationName = null;
    return this;
  }

  @DataBoundConstructor
  public Inspection() {}

  public InspectionReport analyse(TaskListener listener, Revision revision, FilePath workspace)
      throws IOException, InterruptedException {
    return analysisStrategy.analyse(listener, revision, workspace);
  }

  public AnalysisStrategy getAnalysisStrategy() {
    return analysisStrategy;
  }

  @DataBoundSetter
  public void setAnalysisStrategy(AnalysisStrategy analysisStrategy) {
    this.analysisStrategy = analysisStrategy;
  }

  /** @deprecated Use {@link PreviewModeAnalysisStrategy} instead */
  @Deprecated
  @Nullable
  public String getSonarQubeInstallationName() {
    return getOrSpawnPreviewModeAnalysisStrategy().getSonarQubeInstallationName();
  }

  /** @deprecated Use {@link PreviewModeAnalysisStrategy} instead */
  @Deprecated
  @DataBoundSetter
  public void setSonarQubeInstallationName(String name) {
    getOrSetPreviewModeAnalysisStrategy().setSonarQubeInstallationName(name);
  }

  /** @deprecated Use {@link #getSonarQubeInstallationName()} */
  @Deprecated
  @Nonnull
  public String getServerURL() {
    return getOrSpawnPreviewModeAnalysisStrategy()
        .getSonarQubeInstallation()
        .map(SonarInstallation::getServerUrl)
        .orElse(PreviewModeAnalysisStrategy.DescriptorImpl.SONAR_URL);
  }

  /** @deprecated Use {@link #setSonarQubeInstallationName(String)} */
  @Deprecated
  @DataBoundSetter
  public void setServerURL(String serverURL) {

    String installationName =
        Optional.ofNullable(serverURL)
            .filter(StringUtils::isNotBlank)
            .map(name -> SonarQubeInstallations.get().findOrCreate(name))
            .map(SonarInstallation::getName)
            .orElse(null);

    getOrSetPreviewModeAnalysisStrategy().setSonarQubeInstallationName(installationName);
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  public SubJobConfig getBaseConfig() {
    return getOrSpawnPreviewModeAnalysisStrategy().getBaseConfig();
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  @DataBoundSetter
  public void setBaseConfig(SubJobConfig baseConfig) {
    getOrSetPreviewModeAnalysisStrategy().setBaseConfig(baseConfig);
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  public Collection<SubJobConfig> getSubJobConfigs() {
    return getOrSpawnPreviewModeAnalysisStrategy().getSubJobConfigs();
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  @DataBoundSetter
  public void setSubJobConfigs(Collection<SubJobConfig> subJobConfigs) {
    getOrSetPreviewModeAnalysisStrategy().setSubJobConfigs(subJobConfigs);
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  @DataBoundSetter
  public void setType(String type) {
    getOrSetPreviewModeAnalysisStrategy().setType(type);
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  @SuppressWarnings("unused")
  public String getType() {
    return getOrSpawnPreviewModeAnalysisStrategy().getType();
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  public boolean isAutoMatch() {
    return getOrSpawnPreviewModeAnalysisStrategy().isAutoMatch();
  }

  /** @deprecated Moved to {@link PreviewModeAnalysisStrategy} */
  @Deprecated
  @DataBoundSetter
  public void setAutoMatch(boolean autoMatch) {
    getOrSetPreviewModeAnalysisStrategy().setAutoMatch(autoMatch);
  }

  private PreviewModeAnalysisStrategy getOrSpawnPreviewModeAnalysisStrategy() {
    if (analysisStrategy instanceof PreviewModeAnalysisStrategy) {
      return (PreviewModeAnalysisStrategy) analysisStrategy;
    }
    return new PreviewModeAnalysisStrategy();
  }

  private PreviewModeAnalysisStrategy getOrSetPreviewModeAnalysisStrategy() {
    if (analysisStrategy instanceof PreviewModeAnalysisStrategy) {
      return (PreviewModeAnalysisStrategy) analysisStrategy;
    }
    PreviewModeAnalysisStrategy previewModeAnalysisStrategy = new PreviewModeAnalysisStrategy();
    analysisStrategy = new PreviewModeAnalysisStrategy();
    return previewModeAnalysisStrategy;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<Inspection> {

    @SuppressWarnings("unused")
    public List<Descriptor<?>> getAnalysisStrategyDescriptors() {
      Jenkins jenkins = Jenkins.get();
      return ImmutableList.of(jenkins.getDescriptorOrDie(PreviewModeAnalysisStrategy.class));
    }

    @Override
    public String getDisplayName() {
      return "Inspection";
    }
  }
}
