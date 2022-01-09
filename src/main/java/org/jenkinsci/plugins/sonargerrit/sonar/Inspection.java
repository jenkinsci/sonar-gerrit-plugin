package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.PreviewModeAnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 07.12.2017 13:45 $Id$ */
public class Inspection extends AbstractDescribableImpl<Inspection> {

  // Only kept for backward compatibility purpose
  @SuppressWarnings("unused")
  private transient String serverURL;

  private String sonarQubeInstallationName;

  @SuppressWarnings("unused")
  private transient SubJobConfig baseConfig;

  @SuppressWarnings("unused")
  private transient Collection<SubJobConfig> subJobConfigs;

  @SuppressWarnings("unused")
  private transient String type;

  private AnalysisStrategy analysisStrategy = new PreviewModeAnalysisStrategy();

  @SuppressWarnings("unused")
  protected Object readResolve() {
    if (serverURL != null) {
      sonarQubeInstallationName = SonarQubeInstallations.get().findOrCreate(serverURL).getName();
      serverURL = null;
    }
    if (baseConfig != null || subJobConfigs != null || type != null) {
      PreviewModeAnalysisStrategy previewModeAnalysisStrategy = new PreviewModeAnalysisStrategy();
      analysisStrategy = previewModeAnalysisStrategy;
      previewModeAnalysisStrategy.setBaseConfig(baseConfig);
      previewModeAnalysisStrategy.setSubJobConfigs(subJobConfigs);
      previewModeAnalysisStrategy.setType(type);

      baseConfig = null;
      subJobConfigs = null;
      type = null;
    }
    return this;
  }

  @DataBoundConstructor
  public Inspection() {}

  public InspectionReport analyse(TaskListener listener, Revision revision, FilePath workspace)
      throws IOException, InterruptedException {
    return analysisStrategy.analyse(
        listener, revision, workspace, getSonarQubeInstallation().orElse(null));
  }

  @Nullable
  public String getSonarQubeInstallationName() {
    return sonarQubeInstallationName;
  }

  private Optional<SonarInstallation> getSonarQubeInstallation() {
    return Optional.ofNullable(sonarQubeInstallationName)
        .flatMap(name -> SonarQubeInstallations.get().byName(name));
  }

  @DataBoundSetter
  public void setSonarQubeInstallationName(String name) {
    this.sonarQubeInstallationName = StringUtils.defaultIfBlank(name, null);
  }

  public AnalysisStrategy getAnalysisStrategy() {
    return analysisStrategy;
  }

  @DataBoundSetter
  public void setAnalysisStrategy(AnalysisStrategy analysisStrategy) {
    this.analysisStrategy = analysisStrategy;
  }

  /** @deprecated Use {@link #getSonarQubeInstallationName()} */
  @Deprecated
  @Nonnull
  public String getServerURL() {
    return Optional.ofNullable(sonarQubeInstallationName)
        .flatMap(name -> SonarQubeInstallations.get().byName(name))
        .map(SonarInstallation::getServerUrl)
        .orElse(DescriptorImpl.SONAR_URL);
  }

  /** @deprecated Use {@link #setSonarQubeInstallationName(String)} */
  @DataBoundSetter
  public void setServerURL(String serverURL) {
    this.sonarQubeInstallationName =
        Optional.ofNullable(serverURL)
            .filter(StringUtils::isNotBlank)
            .map(name -> SonarQubeInstallations.get().findOrCreate(name))
            .map(SonarInstallation::getName)
            .orElse(null);
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
    this.baseConfig = MoreObjects.firstNonNull(baseConfig, new SubJobConfig());
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
    public static final String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;

    @SuppressWarnings(value = "unused")
    public FormValidation doCheckSonarQubeInstallationName(@QueryParameter String value) {
      return FormValidation.validateRequired(value);
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillSonarQubeInstallationNameItems() {
      return Stream.of(SonarGlobalConfiguration.get().getInstallations())
          .map(SonarInstallation::getName)
          .map(ListBoxModel.Option::new)
          .collect(Collectors.collectingAndThen(Collectors.toList(), ListBoxModel::new));
    }

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
