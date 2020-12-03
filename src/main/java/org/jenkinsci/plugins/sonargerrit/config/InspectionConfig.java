package org.jenkinsci.plugins.sonargerrit.config;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.sonar.SonarClient;
import org.jenkinsci.plugins.sonargerrit.sonar.SonarUtil;
import org.jenkinsci.plugins.sonargerrit.sonar.dto.Component;
import org.jenkinsci.plugins.sonargerrit.sonar.dto.ComponentSearchResult;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 07.12.2017 13:45
 * $Id$
 */
public class InspectionConfig extends AbstractDescribableImpl<InspectionConfig> {
    private DescriptorImpl.AnalysisType analysisType;

    @Nonnull
    private String serverURL = DescriptorImpl.SONAR_URL;

    private String pullRequestKey;

    private String component;

    private SubJobConfig baseConfig;

    @Nonnull
    private Collection<SubJobConfig> subJobConfigs;

    private String type;

    private String sonarInstallationName;

    @DataBoundConstructor
    public InspectionConfig() {
        this(DescriptorImpl.SONAR_URL, null, null, DescriptorImpl.BASE_TYPE, DescriptorImpl.AnalysisType.PREVIEW_MODE); // set default values
    }

    @SuppressFBWarnings(value = "NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR") // subJobConfigs is initialized in setter
    private InspectionConfig(String serverURL, SubJobConfig baseConfig, List<SubJobConfig> subJobConfigs, String type,
            DescriptorImpl.AnalysisType analysisType) {
        setServerURL(serverURL);
        setBaseConfig(baseConfig);
        setSubJobConfigs(subJobConfigs);
        setType(type);
        setAnalysisType(analysisType);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return new DescriptorImpl();
    }

    @Nonnull
    public String getServerURL() {
        return serverURL;
    }

    @DataBoundSetter
    public void setServerURL(String serverURL) {
        this.serverURL = MoreObjects.firstNonNull(Util.fixEmptyAndTrim(serverURL), DescriptorImpl.SONAR_URL);
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

    public String getType() {
        return type;
    }

    public DescriptorImpl.AnalysisType getAnalysisType() {
        // default of field and c'tor has no effect, so do it here
        return analysisType != null ? analysisType : DescriptorImpl.AnalysisType.PREVIEW_MODE;
    }

    @DataBoundSetter
    public void setAnalysisType(DescriptorImpl.AnalysisType analysisType) {
        this.analysisType = analysisType;
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
    public final void setSubJobConfigs(Collection<SubJobConfig> subJobConfigs) {
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

    public String getPullRequestKey() {
        return pullRequestKey;
    }

    @DataBoundSetter
    public void setPullRequestKey(String pullRequestKey) {
        this.pullRequestKey = pullRequestKey;
    }

    public String getComponent() {
        return component;
    }

    @DataBoundSetter
    public void setComponent(String component) {
        this.component = component;
    }

    public List<SonarInstallation> getSonarInstallations() {
        SonarGlobalConfiguration sonarGlobalConfiguration = GlobalConfiguration.all().get(SonarGlobalConfiguration.class);
        return sonarGlobalConfiguration != null ? Arrays.asList(sonarGlobalConfiguration.getInstallations()) : null;
    }

    @DataBoundSetter
    public void setSonarInstallationName(String sonarInstallationName) {
        this.sonarInstallationName = sonarInstallationName;
    }

    public String getSonarInstallationName() {
        return sonarInstallationName;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<InspectionConfig> {
        public enum AnalysisType {
            PREVIEW_MODE,
            PULL_REQUEST
        }

        public static final String ANALYSIS_TYPE_PREVIEW_MODE = AnalysisType.PREVIEW_MODE.name();
        public static final String ANALYSIS_TYPE_PULL_REQUEST = AnalysisType.PULL_REQUEST.name();

        public static final String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;
        public static final String SONAR_PULLREQUEST_KEY = SonarToGerritPublisher.DescriptorImpl.SONAR_PULLREQUEST_KEY;
        public static final String BASE_TYPE = "base";
        public static final String MULTI_TYPE = "multi";
        public static final String DEFAULT_INSPECTION_CONFIG_TYPE = SonarToGerritPublisher.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE;
        public static final boolean AUTO_MATCH = SonarToGerritPublisher.DescriptorImpl.AUTO_MATCH_INSPECTION_AND_REVISION_PATHS;

        private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList(BASE_TYPE, MULTI_TYPE));

        @SuppressWarnings(value = "unused")
        public FormValidation doCheckServerURL(@QueryParameter String value) {
            if (Util.fixEmptyAndTrim(value) == null) {
                return FormValidation.warning(getLocalized("jenkins.plugin.error.sonar.url.empty"));
            }
            try {
                new URL(value);
            } catch (MalformedURLException e) {
                return FormValidation.warning(getLocalized("jenkins.plugin.error.sonar.url.invalid"));
            }
            return FormValidation.ok();

        }

        /**
         * Is only called once, filtering is done in Frontend by a Combo Box
         *
         * @param value component to search for
         * @param sonarInstallationName  specifies the SonarQube server where the components are fetched from
         * @param analysisType only for Analysis Type.PULL_REQUEST the components are inserted into the model's list of components
         * @return model containing a list of components matching the given component name
         * @throws AbortException if Sonar installation cannot be found
         */
        @SuppressWarnings("unused")
        public ComboBoxModel doFillComponentItems(@QueryParameter String value, @QueryParameter String sonarInstallationName,
                @QueryParameter AnalysisType analysisType) throws AbortException {
            if (analysisType == AnalysisType.PULL_REQUEST) {
                SonarClient sonarClient = SonarUtil.getSonarClient(sonarInstallationName);
                String componentKey = SonarUtil.isolateComponentKey(value);
                ComponentSearchResult componentSearchResult = sonarClient.fetchComponent(componentKey);
                return new ComboBoxModel(componentSearchResult.getComponents().stream()
                        .map(c -> c.getName() + " (" + c.getKey() + ")")
                        .collect(Collectors.toList()));
            } else {
                return new ComboBoxModel();
            }
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckComponent(@QueryParameter String value, @QueryParameter String sonarInstallationName,
                @QueryParameter AnalysisType analysisType) throws AbortException {
            if (analysisType == AnalysisType.PULL_REQUEST) {
                SonarClient sonarClient = SonarUtil.getSonarClient(sonarInstallationName);
                String componentKey = SonarUtil.isolateComponentKey(value);
                ComponentSearchResult componentSearchResult = sonarClient.fetchComponent(componentKey);

                if (componentSearchResult.getPaging().getTotal() == 1) {
                    Component component = componentSearchResult.getComponents().get(0);
                    if (!Objects.equals(componentKey, component.getKey())) {
                        return FormValidation
                                .error("Ambiguous project key '" + value + "'. Did you mean '" + component.getKey() + "'?");
                    } else {
                        return FormValidation
                                .ok(component.getName() + ": " + sonarClient.getServerUrl() + "dashboard?id=" + component
                                        .getKey());
                    }
                } else if (componentSearchResult.getPaging().getTotal() > 1) {
                    return FormValidation
                            .error("Multiple results found for '" + componentKey + "' on " + sonarClient.getServerUrl());
                } else {
                    return FormValidation.error("'" + componentKey + "' could not be found on " + sonarClient.getServerUrl());
                }
            } else {
                return FormValidation.ok();
            }
        }

        @Override
        @Nonnull
        public String getDisplayName() {
            return "InspectionConfig";
        }
    }

}
