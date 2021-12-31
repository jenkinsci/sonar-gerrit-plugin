package org.jenkinsci.plugins.sonargerrit.review;

import org.jenkinsci.plugins.sonargerrit.config.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.config.ScoreConfig;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 13:47
 *
 * <p>$Id$
 */
public interface GerritReviewTest {
  String CATEGORY = ScoreConfig.DescriptorImpl.CATEGORY;
  Integer NO_ISSUES_SCORE = ScoreConfig.DescriptorImpl.NO_ISSUES_SCORE;
  Integer SOME_ISSUES_SCORE = ScoreConfig.DescriptorImpl.SOME_ISSUES_SCORE;
  String NO_ISSUES_TITLE_TEMPLATE = ReviewConfig.DescriptorImpl.NO_ISSUES_TITLE_TEMPLATE;
  String SOME_ISSUES_TITLE_TEMPLATE = ReviewConfig.DescriptorImpl.SOME_ISSUES_TITLE_TEMPLATE;

  @SuppressWarnings(value = "unused")
  void testReviewHeader();

  @SuppressWarnings(value = "unused")
  void testOverrideReviewHeader();

  @SuppressWarnings(value = "unused")
  void testReviewComment();

  @SuppressWarnings(value = "unused")
  void testOverrideReviewComment();

  @SuppressWarnings(value = "unused")
  void testScore();

  @SuppressWarnings(value = "unused")
  void testOverrideScore();

  @SuppressWarnings(value = "unused")
  void testCategory();

  @SuppressWarnings(value = "unused")
  void testOverrideCategory();

  @SuppressWarnings(value = "unused")
  void testNoScoreConfig();

  @SuppressWarnings(value = "unused")
  void testOverrideScoreAndCategory();
}
