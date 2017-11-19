package org.jenkinsci.plugins.sonargerrit.review.notification;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 14:40
 * <p/>
 * $Id$
 */
public interface GerritReviewNotificationTest {
    void testNone();

    void testOwner();

    void testOwnerReviewers();

    void testAll();
}
