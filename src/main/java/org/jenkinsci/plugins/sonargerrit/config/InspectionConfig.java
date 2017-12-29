package org.jenkinsci.plugins.sonargerrit.config;

import com.google.common.base.MoreObjects;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 07.12.2017 13:45
 * $Id$
 */
public class InspectionConfig extends AbstractDescribableImpl<InspectionConfig> {
    @Nonnull
    private String serverURL = DescriptorImpl.SONAR_URL;

    private SubJobConfig baseConfig;

    @Nonnull
    private Collection<SubJobConfig> subJobConfigs;

    private String type;

    @DataBoundConstructor
    public InspectionConfig() {
        this(DescriptorImpl.SONAR_URL, null, null, DescriptorImpl.BASE_TYPE); // set default values
    }

    private InspectionConfig(@Nonnull String serverURL, SubJobConfig baseConfig, List<SubJobConfig> subJobConfigs, String type) {
        setServerURL(serverURL);
        setBaseConfig(baseConfig);
        setSubJobConfigs(subJobConfigs);
        setType(type);
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
    public void setServerURL(@Nonnull String serverURL) {
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
        if (subJobConfigs != null && subJobConfigs.size() > 0) {
            this.subJobConfigs = new LinkedList<>(subJobConfigs);
        } else {
            this.subJobConfigs = new LinkedList<>();
            this.subJobConfigs.add(new SubJobConfig());
        }
    }

    public boolean isPathCorrectionNeeded() {
        return isAutoMatch();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<InspectionConfig> {
        public static final String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;
        public static final String BASE_TYPE = "base";
        public static final String MULTI_TYPE = "multi";
        public static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList(BASE_TYPE, MULTI_TYPE));
        public static final String DEFAULT_INSPECTION_CONFIG_TYPE = SonarToGerritPublisher.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE;
        public static final boolean AUTO_MATCH = SonarToGerritPublisher.DescriptorImpl.AUTO_MATCH_INSPECTION_AND_REVISION_PATHS;

        /**
         * Performs on-the-fly validation of the form field 'serverURL'.
         *
         * @param value This parameter receives the value that the user has typed.
         *
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
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

        public String getDisplayName() {
            return "InspectionConfig";
        }
    }
}
