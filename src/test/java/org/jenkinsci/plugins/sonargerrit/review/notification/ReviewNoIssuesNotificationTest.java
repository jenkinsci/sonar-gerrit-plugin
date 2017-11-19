package org.jenkinsci.plugins.sonargerrit.review.notification;

import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.config.NotificationConfig;
import org.junit.Assert;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 14:41
 * <p/>
 * $Id$
 */
public class ReviewNoIssuesNotificationTest extends BaseNotificationTest{
    protected NotifyHandling getDefault() {
        return NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES;
    }

    protected void testNotification(NotifyHandling handling, NotifyHandling other) {
        publisher.getNotificationConfig().setNoIssuesNotificationRecipient(handling.name());
        publisher.getNotificationConfig().setCommentedIssuesNotificationRecipient(other.name());
        publisher.getNotificationConfig().setNegativeScoreNotificationRecipient(other.name());
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(handling, reviewResult.notify);
    }
}
