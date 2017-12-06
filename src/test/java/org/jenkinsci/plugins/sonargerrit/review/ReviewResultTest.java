package org.jenkinsci.plugins.sonargerrit.review;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.ReportBasedTest;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.config.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.config.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarQubeIssueAdapter;
import org.jenkinsci.plugins.sonargerrit.review.GerritReviewBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 12:41
 * $Id$
 */
public class ReviewResultTest extends ReportBasedTest {
    protected Multimap<String, IssueAdapter> scoreIssues = LinkedListMultimap.create();
    protected Multimap<String, IssueAdapter> commentIssues = LinkedListMultimap.create();
    protected SonarToGerritPublisher publisher;

    @Before
    public void initialize() {
        publisher = buildPublisher(Severity.INFO);
    }

    @Test
    public void testNoScoreConfig() {
        publisher.setScoreConfig(null);
        ReviewInput reviewResult = getReviewResult();
        Assert.assertNull(reviewResult.labels);
    }

    protected ReviewInput getReviewResult() {
        GerritReviewBuilder builder = new GerritReviewBuilder(commentIssues, scoreIssues,
                publisher.getReviewConfig(), publisher.getScoreConfig(),
                publisher.getNotificationConfig(), publisher.getSonarURL());
        return builder.buildReview();
    }

    protected ReviewConfig getReviewConfig() {
        return publisher.getReviewConfig();
    }

    public class DummyIssue extends Issue implements IssueAdapter {
        @Override
        public Severity getSeverity() {
            return Severity.CRITICAL;
        }

        @Override
        public String getRule() {
            return "rule";
        }

        @Override
        public String getMessage() {
            return "message";
        }

        @Override
        public String getFilepath(){
            return getComponent();
        }
    }

    protected SonarToGerritPublisher buildPublisher(Severity severity) {
        SonarToGerritPublisher publisher = new SonarToGerritPublisher();
        publisher.setScoreConfig(new ScoreConfig());
        return publisher;
    }


}
