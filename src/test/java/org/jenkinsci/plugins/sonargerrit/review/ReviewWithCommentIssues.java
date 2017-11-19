package org.jenkinsci.plugins.sonargerrit.review;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.ReviewResultTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 18.11.2017 17:33
 * <p/>
 * $Id$
 */
public class ReviewWithCommentIssues extends ReviewResultTest implements GerritReviewTest {

    @Before
    public void initialize() {
        super.initialize();
        commentIssues.put("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java", new DummyIssue());
        commentIssues.put("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager1.java", new DummyIssue());
    }

    @Test
    public void testReviewHeader() {
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals("2 SonarQube violations have been found.", reviewResult.message);
    }

    @Override
    public void testOverrideReviewHeader() {
        getReviewConfig().setSomeIssuesTitleTemplate("Some Issues Header");
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals("Some Issues Header", reviewResult.message);
    }

    @Test
    public void testReviewComment() {
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(2, reviewResult.comments.size());
// todo check comment >?        Assert.assertEquals("", reviewResult.robotComments.get(0).);
    }

    @Override
    public void testOverrideReviewComment() {
        getReviewConfig().setIssueCommentTemplate("That's an Issue!");
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(2, reviewResult.comments.size());
        // todo check text
    }

    // the rest is same as ReviewWithNoIssuesTest
    @Test
    public void testScore() {
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(1, reviewResult.labels.get(CATEGORY).intValue());
    }

    @Test
    public void testCategory() {
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertNotNull(reviewResult.labels.get(CATEGORY));
    }

    @Test
    public void testOverrideScore() {
        publisher.getScoreConfig().setNoIssuesScore(2);
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(2, reviewResult.labels.get(CATEGORY).intValue());
    }

    @Test
    public void testOverrideCategory() {
        publisher.getScoreConfig().setCategory("Other");
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertNull(reviewResult.labels.get(CATEGORY));
        Assert.assertEquals(1, reviewResult.labels.get("Other").intValue());
    }

    @Test
    public void testOverrideScoreAndCategory() {
        publisher.getScoreConfig().setCategory("Other");
        publisher.getScoreConfig().setNoIssuesScore(2);
        ReviewInput reviewResult = getReviewResult();
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertNull(reviewResult.labels.get(CATEGORY));
        Assert.assertNotNull(reviewResult.labels.get("Other"));
        Assert.assertEquals(2, reviewResult.labels.get("Other").intValue());
    }

}
