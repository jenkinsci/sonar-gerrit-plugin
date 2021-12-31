package org.jenkinsci.plugins.sonargerrit.review.notification;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 14:40
 *
 * <p>$Id$
 */
public interface GerritReviewNotificationTest {
  @SuppressWarnings(value = "unused")
  void testNone();

  @SuppressWarnings(value = "unused")
  void testOwner();

  @SuppressWarnings(value = "unused")
  void testOwnerReviewers();

  @SuppressWarnings(value = "unused")
  void testAll();
}
