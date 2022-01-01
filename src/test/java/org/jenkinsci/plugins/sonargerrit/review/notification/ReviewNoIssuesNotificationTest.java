package org.jenkinsci.plugins.sonargerrit.review.notification;

import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.config.NotificationConfig;
import org.junit.jupiter.api.Assertions;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 14:41
 *
 * <p>$Id$
 */
public class ReviewNoIssuesNotificationTest extends BaseNotificationTest {
  @Override
  protected NotifyHandling getDefault() {
    return NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES;
  }

  @Override
  protected void testNotification(NotifyHandling handling, NotifyHandling other) {
    publisher.getNotificationConfig().setNoIssuesNotificationRecipient(handling.name());
    publisher.getNotificationConfig().setCommentedIssuesNotificationRecipient(other.name());
    publisher.getNotificationConfig().setNegativeScoreNotificationRecipient(other.name());
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(handling, reviewResult.notify);
  }
}
