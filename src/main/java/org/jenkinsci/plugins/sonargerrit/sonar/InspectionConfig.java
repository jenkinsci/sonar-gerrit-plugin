package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.MoreObjects;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 07.12.2017 13:45 $Id$ */
public class InspectionConfig extends AbstractDescribableImpl<InspectionConfig> {

  // Only kept for backward compatibility purpose
  @SuppressWarnings("unused")
  private transient String serverURL;

  private String sonarQubeInstallationName;

  private SubJobConfig baseConfig;

  @Nonnull private Collection<SubJobConfig> subJobConfigs = new LinkedList<>();

  private String type;

  @DataBoundConstructor
  public InspectionConfig() {
    setBaseConfig(null);
    setSubJobConfigs(null);
    setType(DescriptorImpl.BASE_TYPE);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return new DescriptorImpl();
  }

  @Nullable
  public String getSonarQubeInstallationName() {
    return sonarQubeInstallationName;
  }

  @DataBoundSetter
  public void setSonarQubeInstallationName(String name) {
    this.sonarQubeInstallationName = StringUtils.defaultIfBlank(name, null);
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
    if (DescriptorImpl.ALLOWED_TYPES.contains(type)) {
      this.type = type;
    }
  }

  @SuppressWarnings("unused")
  public String getType() {
    return type;
  }

  public boolean isMultiConfigMode() {
    return isType(DescriptorImpl.MULTI_TYPE);
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

  @SuppressWarnings("unused")
  protected Object readResolve() {
    if (serverURL != null) {
      sonarQubeInstallationName = SonarQubeInstallations.get().findOrCreate(serverURL).getName();
    }
    return this;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<InspectionConfig> {
    public static final String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;
    public static final String BASE_TYPE = "base";
    public static final String MULTI_TYPE = "multi";
    public static final String DEFAULT_INSPECTION_CONFIG_TYPE =
        SonarToGerritPublisher.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE;
    public static final boolean AUTO_MATCH =
        SonarToGerritPublisher.DescriptorImpl.AUTO_MATCH_INSPECTION_AND_REVISION_PATHS;

    private static final Set<String> ALLOWED_TYPES =
        new HashSet<>(Arrays.asList(BASE_TYPE, MULTI_TYPE));

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

    @Override
    public String getDisplayName() {
      return "InspectionConfig";
    }
  }
}
