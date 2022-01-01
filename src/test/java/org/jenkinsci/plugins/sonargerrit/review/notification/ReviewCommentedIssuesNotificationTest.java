package org.jenkinsci.plugins.sonargerrit.review.notification;

import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.config.NotificationConfig;
import org.junit.jupiter.api.Assertions;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 14:42
 *
 * <p>$Id$
 */
public class ReviewCommentedIssuesNotificationTest extends BaseNotificationTest {
  @Override
  protected void doInitialize() {
    super.doInitialize();
    commentIssues.put(
        "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
        new DummyIssue());
  }

  @Override
  protected NotifyHandling getDefault() {
    return NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_COMMENTED_ISSUES;
  }

  @Override
  protected void testNotification(NotifyHandling handling, NotifyHandling other) {
    publisher.getNotificationConfig().setNoIssuesNotificationRecipient(other.name());
    publisher.getNotificationConfig().setCommentedIssuesNotificationRecipient(handling.name());
    publisher.getNotificationConfig().setNegativeScoreNotificationRecipient(other.name());
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(handling, reviewResult.notify);
  }
}
