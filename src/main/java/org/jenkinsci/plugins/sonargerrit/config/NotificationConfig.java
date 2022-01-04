package org.jenkinsci.plugins.sonargerrit.config;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.google.common.base.MoreObjects;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.NotifyHandling;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.util.DataHelper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 09.11.2017 14:27 $Id$ */
public class NotificationConfig extends AbstractDescribableImpl<NotificationConfig> {

  /*
   *  Recipient of a notification to be sent in case if there are no issues matching either of filters found
   * */
  @Nonnull
  private String noIssuesNotificationRecipient =
      DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES.name();

  /*
   *  Recipient of a notification to be sent in case if there are issues matching comment filter found
   * */
  @Nonnull
  private String commentedIssuesNotificationRecipient =
      DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES.name();

  /*
   *  Recipient of a notification to be sent in case if there are issues matching score filter found
   * */
  @Nonnull
  private String negativeScoreNotificationRecipient =
      DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE.name();

  public NotificationConfig(
      String noIssuesNotificationRecipient,
      String commentedIssuesNotificationRecipient,
      String negativeScoreNotificationRecipient) {
    setNoIssuesNotificationRecipient(noIssuesNotificationRecipient);
    setCommentedIssuesNotificationRecipient(commentedIssuesNotificationRecipient);
    setNegativeScoreNotificationRecipient(negativeScoreNotificationRecipient);
  }

  public NotificationConfig(
      @Nonnull NotifyHandling noIssuesNotificationRecipient,
      @Nonnull NotifyHandling commentedIssuesNotificationRecipient,
      @Nonnull NotifyHandling negativeScoreNotificationRecipient) {
    this(
        noIssuesNotificationRecipient.name(),
        commentedIssuesNotificationRecipient.name(),
        negativeScoreNotificationRecipient.name());
  }

  @DataBoundConstructor
  public NotificationConfig() {
    this(
        DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES,
        DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES,
        DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE);
  }

  @Nonnull
  public String getNoIssuesNotificationRecipient() {
    return noIssuesNotificationRecipient;
  }

  @DataBoundSetter
  public void setNoIssuesNotificationRecipient(String noIssuesNotificationRecipient) {
    noIssuesNotificationRecipient =
        DataHelper.checkEnumValueCorrect(NotifyHandling.class, noIssuesNotificationRecipient);
    this.noIssuesNotificationRecipient =
        MoreObjects.firstNonNull(
            noIssuesNotificationRecipient, DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES_STR);
  }

  @Nonnull
  public String getCommentedIssuesNotificationRecipient() {
    return commentedIssuesNotificationRecipient;
  }

  @DataBoundSetter
  public void setCommentedIssuesNotificationRecipient(String commentedIssuesNotificationRecipient) {
    commentedIssuesNotificationRecipient =
        DataHelper.checkEnumValueCorrect(
            NotifyHandling.class, commentedIssuesNotificationRecipient);
    this.commentedIssuesNotificationRecipient =
        MoreObjects.firstNonNull(
            commentedIssuesNotificationRecipient,
            DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES_STR);
  }

  @Nonnull
  public String getNegativeScoreNotificationRecipient() {
    return negativeScoreNotificationRecipient;
  }

  @DataBoundSetter
  public void setNegativeScoreNotificationRecipient(String negativeScoreNotificationRecipient) {
    negativeScoreNotificationRecipient =
        DataHelper.checkEnumValueCorrect(NotifyHandling.class, negativeScoreNotificationRecipient);
    this.negativeScoreNotificationRecipient =
        MoreObjects.firstNonNull(
            negativeScoreNotificationRecipient,
            DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE_STR);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return new DescriptorImpl();
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<NotificationConfig> {
    public static final NotifyHandling NOTIFICATION_RECIPIENT_NO_ISSUES =
        SonarToGerritPublisher.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES;
    public static final NotifyHandling NOTIFICATION_RECIPIENT_COMMENTED_ISSUES =
        SonarToGerritPublisher.DescriptorImpl.NOTIFICATION_RECIPIENT_SOME_ISSUES;
    public static final NotifyHandling NOTIFICATION_RECIPIENT_NEGATIVE_SCORE =
        SonarToGerritPublisher.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE;

    public static final String NOTIFICATION_RECIPIENT_NO_ISSUES_STR =
        NOTIFICATION_RECIPIENT_NO_ISSUES.name();
    public static final String NOTIFICATION_RECIPIENT_COMMENTED_ISSUES_STR =
        NOTIFICATION_RECIPIENT_COMMENTED_ISSUES.name();
    public static final String NOTIFICATION_RECIPIENT_NEGATIVE_SCORE_STR =
        NOTIFICATION_RECIPIENT_NEGATIVE_SCORE.name();

    /**
     * Performs on-the-fly validation of the form field 'noIssuesNotificationRecipient'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckNoIssuesNotificationRecipient(@QueryParameter String value) {
      return checkNotificationType(value);
    }

    /**
     * Performs on-the-fly validation of the form field 'commentedIssuesNotificationRecipient'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckCommentedIssuesNotificationRecipient(
        @QueryParameter String value) {
      return checkNotificationType(value);
    }

    /**
     * Performs on-the-fly validation of the form field 'negativeScoreNotificationRecipient'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckNegativeScoreNotificationRecipient(@QueryParameter String value) {
      return checkNotificationType(value);
    }

    private FormValidation checkNotificationType(@QueryParameter String value) {
      if (value == null) {
        return FormValidation.error(
            getLocalized("jenkins.plugin.error.review.notification.recipient.unknown"));
      }
      return Stream.of(NotifyHandling.values())
          .filter(handling -> value.equals(handling.name()))
          .findFirst()
          .map(handling -> FormValidation.ok())
          .orElseGet(
              () ->
                  FormValidation.error(
                      getLocalized("jenkins.plugin.error.review.notification.recipient.unknown")));
    }

    @Override
    public String getDisplayName() {
      return "NotificationConfig";
    }
  }
}
