package org.jenkinsci.plugins.sonargerrit.review.notification;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 15:06
 * <p>
 * $Id$
 */
public class ReviewCommentAndScoreNotificationTest extends ReviewNegativeScoreNotificationTest {
    @Override
    public void initialize() {
        super.initialize();
        commentIssues.put("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java", new DummyIssue());
    }
}
