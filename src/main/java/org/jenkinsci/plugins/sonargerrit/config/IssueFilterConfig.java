package org.jenkinsci.plugins.sonargerrit.config;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.data.entity.Severity;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 11.10.2017 19:53
 * $Id$
 */

public class IssueFilterConfig extends AbstractDescribableImpl<IssueFilterConfig> {
    private String severity;       // todo make enum
    private boolean newIssuesOnly;
    private boolean changedLinesOnly;

    public IssueFilterConfig(String severity, boolean newIssuesOnly, boolean changedLinesOnly) {
        this.severity = severity;
        this.newIssuesOnly = newIssuesOnly;
        this.changedLinesOnly = changedLinesOnly;
    }

    @DataBoundConstructor
    public IssueFilterConfig() {
        this(DescriptorImpl.SEVERITY, DescriptorImpl.NEW_ISSUES_ONLY, DescriptorImpl.NEW_ISSUES_ONLY);
    }

    public String getSeverity() {
        return severity;
    }

    public boolean isChangedLinesOnly() {
        return changedLinesOnly;
    }

    public boolean isNewIssuesOnly() {
        return newIssuesOnly;
    }

    @DataBoundSetter
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @DataBoundSetter
    public void setNewIssuesOnly(boolean newIssuesOnly) {
        this.newIssuesOnly = newIssuesOnly;
    }

    @DataBoundSetter
    public void setChangedLinesOnly(boolean changedLinesOnly) {
        this.changedLinesOnly = changedLinesOnly;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<IssueFilterConfig> {
        public static final String SEVERITY = SonarToGerritPublisher.DescriptorImpl.SEVERITY;
        public static final boolean NEW_ISSUES_ONLY = SonarToGerritPublisher.DescriptorImpl.NEW_ISSUES_ONLY;
        public static final boolean CHANGED_LINES_ONLY = SonarToGerritPublisher.DescriptorImpl.CHANGED_LINES_ONLY;

        /**
         * Performs on-the-fly validation of the form field 'severity'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckSeverity(@QueryParameter String value) {
            if (value == null || Severity.valueOf(value) == null) {
                return FormValidation.error(getLocalized("jenkins.plugin.error.review.filter.severity.unknown"));
            }
            return FormValidation.ok();
        }

        public String getDisplayName() {
            return "IssueFilterConfig";
        }
    }
}
