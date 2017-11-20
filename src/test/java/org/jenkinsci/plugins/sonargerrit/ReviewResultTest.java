package org.jenkinsci.plugins.sonargerrit;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.config.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.config.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
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
    protected Multimap<String, Issue> scoreIssues = LinkedListMultimap.create();
    protected Multimap<String, Issue> commentIssues = LinkedListMultimap.create();
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
        return publisher.getReviewResult(commentIssues, scoreIssues);
    }

    protected ReviewConfig getReviewConfig() {
        return publisher.getReviewConfig();
    }

    public class DummyIssue extends Issue {
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
    }

    protected SonarToGerritPublisher buildPublisher(Severity severity) {
        SonarToGerritPublisher publisher = new SonarToGerritPublisher();
        publisher.setScoreConfig(new ScoreConfig());
        return publisher;
    }


}
