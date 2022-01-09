package org.jenkinsci.plugins.sonargerrit.gerrit;

import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.NotifyHandling;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.jupiter.api.Assertions;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 14:42
 *
 * <p>$Id$
 */
public class ReviewNegativeScoreNotificationTest extends BaseNotificationTest {
  @Override
  protected void doInitialize() {
    super.doInitialize();
    scoreIssues.put(
        "juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
        new DummyIssue());
  }

  @Override
  protected NotifyHandling getDefault() {
    return NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE;
  }

  @Override
  protected void testNotification(NotifyHandling handling, NotifyHandling other) {
    publisher.getNotificationConfig().setNoIssuesNotificationRecipient(other.name());
    publisher.getNotificationConfig().setCommentedIssuesNotificationRecipient(other.name());
    publisher.getNotificationConfig().setNegativeScoreNotificationRecipient(handling.name());
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertEquals(handling, reviewResult.notify);
  }
}
