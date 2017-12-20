package org.jenkinsci.plugins.sonargerrit.config;

import com.google.common.base.MoreObjects;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 02.12.2015 12:11
 */

public class SubJobConfig extends AbstractDescribableImpl<SubJobConfig> {

    @Nonnull
    private String projectPath;

    @Nonnull
    private String sonarReportPath;

    private boolean autoMatch;

    public SubJobConfig(String projectPath, String sonarReportPath) {
        setProjectPath(projectPath);
        setSonarReportPath(sonarReportPath);
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
        this.projectPath = MoreObjects.firstNonNull(projectPath, SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH);
    }

    @DataBoundSetter
    public void setSonarReportPath(String sonarReportPath) {
        this.sonarReportPath = MoreObjects.firstNonNull(sonarReportPath, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<SubJobConfig> {
        public static final String PROJECT_PATH = SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH;
        public static final String SONAR_REPORT_PATH = SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH;

//        /**
//         * Performs on-the-fly validation of the form field 'serverURL'.
//         *
//         * @param value This parameter receives the value that the user has typed.
//         *
//         * @return Indicates the outcome of the validation. This is sent to the browser.
//         * <p>
//         * Note that returning {@link FormValidation#error(String)} does not
//         * prevent the form from being saved. It just means that a message
//         * will be displayed to the user.
//         */
//        @SuppressWarnings(value = "unused")
//        public FormValidation doCheckServerURL(@QueryParameter String value) {
//            if (Util.fixEmptyAndTrim(value) == null) {
//                return FormValidation.warning(getLocalized("jenkins.plugin.error.sonar.url.empty"));
//            }
//            try {
//                new URL(value);
//            } catch (MalformedURLException e) {
//                return FormValidation.warning(getLocalized("jenkins.plugin.error.sonar.url.invalid"));
//            }
//            return FormValidation.ok();
//
//        }

        public String getDisplayName() {
            return "SubJobConfig";
        }
    }
}


