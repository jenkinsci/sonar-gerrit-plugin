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

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 09.11.2017 14:02
 * $Id$
 */
public class ReviewConfig extends AbstractDescribableImpl<ReviewConfig> {
    /*
   * Filter to be used to extract issues that need to be commented in Gerrit
   * */
    @Nonnull
    private IssueFilterConfig issueFilterConfig = new IssueFilterConfig();

    /*
    *  Gerrit review title when matching the review filter issues are found and commented
    * */
    @Nonnull
    private String someIssuesTitleTemplate = DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE;

    /*
    *  Gerrit review title when matching the review filter issues are not found (no comments)
    * */
    @Nonnull
    private String noIssuesTitleTemplate = DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE;

    /*
    * Gerrit issue comment template
    * */
    @Nonnull
    private String issueCommentTemplate = DescriptorImpl.ISSUE_COMMENT_TEMPLATE;

    public ReviewConfig(@Nonnull IssueFilterConfig issueFilterConfig, @Nonnull String noIssuesTitleTemplate, @Nonnull String someIssuesTitleTemplate, @Nonnull String issueCommentTemplate) {
        setIssueFilterConfig(issueFilterConfig);
        setNoIssuesTitleTemplate(noIssuesTitleTemplate);
        setSomeIssuesTitleTemplate(someIssuesTitleTemplate);
        setIssueCommentTemplate(issueCommentTemplate);
    }

    @DataBoundConstructor
    public ReviewConfig() {
        this(new IssueFilterConfig(), DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE, DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE, DescriptorImpl.ISSUE_COMMENT_TEMPLATE);
    }

    public IssueFilterConfig getIssueFilterConfig() {
        return issueFilterConfig;
    }

    @DataBoundSetter
    public void setIssueFilterConfig(IssueFilterConfig issueFilterConfig) {
        this.issueFilterConfig = MoreObjects.firstNonNull(issueFilterConfig, new IssueFilterConfig());
    }

    @Nonnull
    public String getSomeIssuesTitleTemplate() {
        return someIssuesTitleTemplate;
    }

    @DataBoundSetter
    public void setSomeIssuesTitleTemplate(@Nonnull String someIssuesTitleTemplate) {
        this.someIssuesTitleTemplate = MoreObjects.firstNonNull(Util.fixEmptyAndTrim(someIssuesTitleTemplate), DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE);
    }

    @Nonnull
    public String getNoIssuesTitleTemplate() {
        return noIssuesTitleTemplate;
    }

    @DataBoundSetter
    public void setNoIssuesTitleTemplate(@Nonnull String noIssuesTitleTemplate) {
        this.noIssuesTitleTemplate = MoreObjects.firstNonNull(Util.fixEmptyAndTrim(noIssuesTitleTemplate), DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE);
    }

    @Nonnull
    public String getIssueCommentTemplate() {
        return issueCommentTemplate;
    }

    @DataBoundSetter
    public void setIssueCommentTemplate(@Nonnull String issueCommentTemplate) {
        this.issueCommentTemplate = MoreObjects.firstNonNull(Util.fixEmptyAndTrim(issueCommentTemplate), DescriptorImpl.ISSUE_COMMENT_TEMPLATE);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ReviewConfig> {
        public static final String NO_ISSUES_TITLE_TEMPLATE = SonarToGerritPublisher.DescriptorImpl.NO_ISSUES_TEXT;
        public static final String SOME_ISSUES_TITLE_TEMPLATE = SonarToGerritPublisher.DescriptorImpl.SOME_ISSUES_TEXT;
        public static final String ISSUE_COMMENT_TEMPLATE = SonarToGerritPublisher.DescriptorImpl.ISSUE_COMMENT_TEXT;

        /**
         * Performs on-the-fly validation of the form field 'noIssuesTitleTemplate'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckNoIssuesTitleTemplate(@QueryParameter String value) {
            return FormValidation.validateRequired(value);
        }

        /**
         * Performs on-the-fly validation of the form field 'someIssuesTitleTemplate'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckSomeIssuesTitleTemplate(@QueryParameter String value) {
            return FormValidation.validateRequired(value);
        }

        /**
         * Performs on-the-fly validation of the form field 'issueCommentTemplate'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckIssueCommentTemplate(@QueryParameter String value) {
            return FormValidation.validateRequired(value);
        }

        @Override
        public String getDisplayName() {
            return "ReviewConfig";
        }
    }
}
