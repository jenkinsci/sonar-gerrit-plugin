package org.jenkinsci.plugins.sonargerrit.config;

import com.google.common.base.MoreObjects;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private Collection<SubJobConfig> subJobConfigs = new LinkedList<>();

    @DataBoundConstructor
    public InspectionConfig() {
        this(DescriptorImpl.SONAR_URL, new SubJobConfig(), new LinkedList<SubJobConfig>());
    }

    public InspectionConfig(@Nonnull String serverURL, SubJobConfig baseConfig, List<SubJobConfig> subJobConfigs) {
        setServerURL(serverURL);
        setBaseConfig(baseConfig);
        setSubJobConfigs(subJobConfigs);
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
        this.baseConfig = baseConfig;
    }

    public Collection<SubJobConfig> getSubJobConfigs() {
        return subJobConfigs;
    }

    public Collection<SubJobConfig> getAllSubJobConfigs() {
        return getDescriptor().multiConfigMode ? Collections.singletonList(baseConfig) : subJobConfigs;
    }

    @DataBoundSetter
    public void setSubJobConfigs(@Nonnull Collection<SubJobConfig> subJobConfigs) {
        this.subJobConfigs = MoreObjects.firstNonNull(subJobConfigs, new LinkedList<SubJobConfig>());
    }

    public boolean isPathCorrectionNeeded(){
        return baseConfig != null &&  baseConfig.isAutoMatch();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<InspectionConfig> {
        public static final String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;

        private boolean multiConfigMode;

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
            final JSONObject languageJSON = formData.getJSONObject("multiConfigMode");
            if ((languageJSON != null) && !(languageJSON.isNullObject())) {
                this.multiConfigMode = true;
            } else {
                this.multiConfigMode = false;
            }
            save();
            return super.configure(req, formData);
        }


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
