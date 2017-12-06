package org.jenkinsci.plugins.sonargerrit.review.notification;

import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.config.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarQubeIssueAdapter;
import org.junit.Assert;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 14:42
 * <p>
 * $Id$
 */
public class ReviewNegativeScoreNotificationTest extends BaseNotificationTest {
    @Override
    public void initialize() {
        super.initialize();
        scoreIssues.put("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java", new DummyIssue());
    }

    protected NotifyHandling getDefault() {
        return NotificationConfig.DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE;
    }

    protected void testNotification(NotifyHandling handling, NotifyHandling other) {
        publisher.getNotificationConfig().setNoIssuesNotificationRecipient(other.name());
        publisher.getNotificationConfig().setCommentedIssuesNotificationRecipient(other.name());
        publisher.getNotificationConfig().setNegativeScoreNotificationRecipient(handling.name());
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(handling, reviewResult.notify);
    }
}
