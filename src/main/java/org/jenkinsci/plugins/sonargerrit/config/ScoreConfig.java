package org.jenkinsci.plugins.sonargerrit.config;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.google.common.base.MoreObjects;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 09.11.2017 13:20 $Id$ */
public class ScoreConfig extends AbstractDescribableImpl<ScoreConfig> {
  /*
   * Filter to be used to extract issues that need to be commented in Gerrit
   * */
  private IssueFilterConfig issueFilterConfig = new IssueFilterConfig();

  /*
   * A score to be applied to the category in case if there are no issues matching score issue filter found
   * */
  @Nonnull private Integer noIssuesScore = DescriptorImpl.NO_ISSUES_SCORE;

  /*
   * A score to be applied to the category in case if there are issues matching score issue filter found
   * */
  @Nonnull private Integer issuesScore = DescriptorImpl.SOME_ISSUES_SCORE;

  /*
   * A category - a label in Gerrit terms - to be applied to score in case it is to be posted
   * */
  @Nonnull private String category = DescriptorImpl.CATEGORY;

  public ScoreConfig(
      IssueFilterConfig issueFilterConfig,
      String category,
      Integer noIssuesScore,
      Integer issuesScore) {
    setIssueFilterConfig(issueFilterConfig);
    setCategory(category);
    setNoIssuesScore(noIssuesScore);
    setIssuesScore(issuesScore);
  }

  @DataBoundConstructor
  public ScoreConfig() {
    this(
        new IssueFilterConfig(),
        DescriptorImpl.CATEGORY,
        DescriptorImpl.NO_ISSUES_SCORE,
        DescriptorImpl.SOME_ISSUES_SCORE);
  }

  public IssueFilterConfig getIssueFilterConfig() {
    return issueFilterConfig;
  }

  @DataBoundSetter
  public void setIssueFilterConfig(IssueFilterConfig scoreIssueFilterConfig) {
    this.issueFilterConfig =
        MoreObjects.firstNonNull(scoreIssueFilterConfig, new IssueFilterConfig());
  }

  @Nonnull
  public Integer getNoIssuesScore() {
    return noIssuesScore;
  }

  @DataBoundSetter
  public void setNoIssuesScore(@Nonnull Integer noIssuesScore) {
    this.noIssuesScore = MoreObjects.firstNonNull(noIssuesScore, DescriptorImpl.NO_ISSUES_SCORE);
  }

  @Nonnull
  public Integer getIssuesScore() {
    return issuesScore;
  }

  @DataBoundSetter
  public void setIssuesScore(@Nonnull Integer issuesScore) {
    this.issuesScore = MoreObjects.firstNonNull(issuesScore, DescriptorImpl.SOME_ISSUES_SCORE);
  }

  @Nonnull
  public String getCategory() {
    return category;
  }

  @DataBoundSetter
  public void setCategory(@Nonnull String category) {
    this.category =
        MoreObjects.firstNonNull(Util.fixEmptyAndTrim(category), DescriptorImpl.CATEGORY);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return new DescriptorImpl();
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<ScoreConfig> {
    public static final String CATEGORY = SonarToGerritPublisher.DescriptorImpl.CATEGORY;
    public static final Integer NO_ISSUES_SCORE =
        SonarToGerritPublisher.DescriptorImpl.NO_ISSUES_SCORE;
    public static final Integer SOME_ISSUES_SCORE =
        SonarToGerritPublisher.DescriptorImpl.SOME_ISSUES_SCORE;

    /**
     * Performs on-the-fly validation of the form field 'noIssuesScore'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckNoIssuesScore(@QueryParameter String value) {
      return checkScore(value);
    }

    /**
     * Performs on-the-fly validation of the form field 'issuesScore'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckIssuesScore(@QueryParameter String value) {
      return checkScore(value);
    }

    private FormValidation checkScore(@QueryParameter String value) {
      try {
        Integer.parseInt(value);
      } catch (NumberFormatException e) {
        return FormValidation.error(getLocalized("jenkins.plugin.error.review.score.not.numeric"));
      }
      return FormValidation.ok();
    }

    /**
     * Performs on-the-fly validation of the form field 'category'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckCategory(@QueryParameter String value) {
      return FormValidation.validateRequired(value);
    }

    public String getDisplayName() {
      return "ScoreConfig";
    }
  }
}
