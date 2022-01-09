package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import com.google.common.base.MoreObjects;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.plugins.sonar.SonarInstallation;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.Revision;
import org.jenkinsci.plugins.sonargerrit.sonar.AnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionReport;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** @author Réda Housni Alaoui */
public class PreviewModeAnalysisStrategy
    extends AbstractDescribableImpl<PreviewModeAnalysisStrategy> implements AnalysisStrategy {

  private SubJobConfig baseConfig;

  @Nonnull private Collection<SubJobConfig> subJobConfigs = new LinkedList<>();

  private String type;

  @DataBoundConstructor
  public PreviewModeAnalysisStrategy() {
    setBaseConfig(null);
    setSubJobConfigs(null);
    setType(PreviewModeAnalysisStrategy.DescriptorImpl.BASE_TYPE);
  }

  public SubJobConfig getBaseConfig() {
    return baseConfig;
  }

  @DataBoundSetter
  public void setBaseConfig(SubJobConfig baseConfig) {
    this.baseConfig = MoreObjects.firstNonNull(baseConfig, new SubJobConfig());
  }

  public Collection<SubJobConfig> getSubJobConfigs() {
    return subJobConfigs;
  }

  public Collection<SubJobConfig> getAllSubJobConfigs() {
    return isMultiConfigMode() ? subJobConfigs : Collections.singletonList(baseConfig);
  }

  public boolean isType(String type) {
    return this.type.equalsIgnoreCase(type);
  }

  @DataBoundSetter
  public void setType(String type) {
    if (PreviewModeAnalysisStrategy.DescriptorImpl.ALLOWED_TYPES.contains(type)) {
      this.type = type;
    }
  }

  @SuppressWarnings("unused")
  public String getType() {
    return type;
  }

  public boolean isMultiConfigMode() {
    return isType(PreviewModeAnalysisStrategy.DescriptorImpl.MULTI_TYPE);
  }

  public boolean isAutoMatch() {
    return !isMultiConfigMode() && baseConfig.isAutoMatch();
  }

  @DataBoundSetter
  public void setAutoMatch(boolean autoMatch) {
    if (!isMultiConfigMode()) {
      baseConfig.setAutoMatch(autoMatch);
    }
  }

  @DataBoundSetter
  public void setSubJobConfigs(Collection<SubJobConfig> subJobConfigs) {
    if (subJobConfigs != null && !subJobConfigs.isEmpty()) {
      this.subJobConfigs = new LinkedList<>(subJobConfigs);
    } else {
      this.subJobConfigs = new LinkedList<>();
      this.subJobConfigs.add(new SubJobConfig());
    }
  }

  public boolean isPathCorrectionNeeded() {
    return isAutoMatch();
  }

  @Override
  public InspectionReport analyse(
      TaskListener listener,
      Revision revision,
      FilePath workspace,
      @Nullable SonarInstallation sonarQubeInstallation)
      throws IOException, InterruptedException {
    return new SonarConnector(listener, this, revision, sonarQubeInstallation)
        .readSonarReports(workspace);
  }

  @Symbol("previewMode")
  @Extension
  public static class DescriptorImpl extends Descriptor<PreviewModeAnalysisStrategy> {
    public static final String BASE_TYPE = "base";
    public static final String MULTI_TYPE = "multi";
    public static final String DEFAULT_INSPECTION_CONFIG_TYPE =
        SonarToGerritPublisher.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE;
    public static final boolean AUTO_MATCH =
        SonarToGerritPublisher.DescriptorImpl.AUTO_MATCH_INSPECTION_AND_REVISION_PATHS;

    private static final Set<String> ALLOWED_TYPES =
        new HashSet<>(Arrays.asList(BASE_TYPE, MULTI_TYPE));

    @Override
    public String getDisplayName() {
      return "Preview mode analysis";
    }
  }
}
