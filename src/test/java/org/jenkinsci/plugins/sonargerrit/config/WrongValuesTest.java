package org.jenkinsci.plugins.sonargerrit.config;

import junit.framework.Assert;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Test;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 19.11.2017 22:15
 * <p/>
 * $Id$
 */
public class WrongValuesTest implements DetailedConfigTest {
    @Test
    public void testSeverity() {
        IssueFilterConfig config = new IssueFilterConfig();
        config.setSeverity("Test");
        Assert.assertEquals(SEVERITY, config.getSeverity());
    }

    @Override
    public void testNewOnly() {
        // nothing to check here
    }

    @Override
    public void testChangedLinesOnly() {
        // nothing to check here
    }

    @Test
    public void testNoIssuesTitleTemplate() {
        ReviewConfig config = new ReviewConfig();
        config.setNoIssuesTitleTemplate("");
        Assert.assertEquals(NO_ISSUES_TITLE_TEMPLATE, config.getNoIssuesTitleTemplate());
    }

    @Test
    public void testSomeIssuesTitleTemplate() {
        ReviewConfig config = new ReviewConfig();
        config.setSomeIssuesTitleTemplate("");
        Assert.assertEquals(SOME_ISSUES_TITLE_TEMPLATE, config.getSomeIssuesTitleTemplate());
    }

    @Test
    public void testIssuesCommentTemplate() {
        ReviewConfig config = new ReviewConfig();
        config.setIssueCommentTemplate("");
        Assert.assertEquals(ISSUE_COMMENT_TEMPLATE, config.getIssueCommentTemplate());
    }

    @Test
    public void testCategory() {
        ScoreConfig config = new ScoreConfig();
        config.setCategory("");
        Assert.assertEquals(CATEGORY, config.getCategory());
    }

    @Test
    public void testNoIssuesScoreScore() {
//        ScoreConfig config = new ScoreConfig();
//        config.setNoIssuesScore(-50); ?

        SonarToGerritPublisher p = new SonarToGerritPublisher();
        p.setPostScore(true);
        p.setNoIssuesScore("test");
        Assert.assertEquals(NO_ISSUES_SCORE, p.getScoreConfig().getNoIssuesScore());
    }

    @Test
    public void testSomeIssuesScoreScore() {
        //        ScoreConfig config = new ScoreConfig();
//        config.setIssuesScore(-50); ?

        SonarToGerritPublisher p = new SonarToGerritPublisher();
        p.setPostScore(true);
        p.setIssuesScore("test");
        Assert.assertEquals(SOME_ISSUES_SCORE, p.getScoreConfig().getIssuesScore());
    }

    @Test
    public void testNoIssuesNotificationRecipient() {
        NotificationConfig config = new NotificationConfig();
        config.setNoIssuesNotificationRecipient("");
        Assert.assertEquals(NO_ISSUES_NOTIFICATION, config.getNoIssuesNotificationRecipient());
    }

    @Test
    public void testIssuesNotificationRecipient() {
        NotificationConfig config = new NotificationConfig();
        config.setCommentedIssuesNotificationRecipient("");
        Assert.assertEquals(ISSUES_NOTIFICATION, config.getCommentedIssuesNotificationRecipient());
    }

    @Test
    public void testNegativeScoreNotificationRecipient() {
        NotificationConfig config = new NotificationConfig();
        config.setNegativeScoreNotificationRecipient("");
        Assert.assertEquals(SCORE_NOTIFICATION, config.getNegativeScoreNotificationRecipient());
    }

    @Test
    public void testSonarUrl() {
        SonarToGerritPublisher p = new SonarToGerritPublisher();
        p.setSonarURL("");
        Assert.assertEquals(SONAR_URL, p.getSonarURL());
    }

    @Test
    public void testSonarReportPath() {
        SonarToGerritPublisher p = new SonarToGerritPublisher();
        p.setPath("fghdfh^%&$(&*IOUM V");
        SubJobConfig subJobConfig = p.getSubJobConfigs().get(0);
        Assert.assertEquals("fghdfh^%&$(&*IOUM V", subJobConfig.getSonarReportPath());

        subJobConfig.setSonarReportPath("fghdfh^%&$(&*IOUM V");
        Assert.assertEquals("fghdfh^%&$(&*IOUM V", subJobConfig.getSonarReportPath());
    }

    @Override
    public void testFilterConfig() {
       // nope
    }

    @Override
    public void testReviewConfig() {
        // nope
    }

    @Override
    public void testScoreConfig() {
        // nope
    }

    @Override
    public void testNotificationConfig() {
        // nope
    }

    @Override
    public void testAuthenticationConfig() {
        // nope
    }

    @Override
    public void testSonarConfig() {
        // nope
    }

    @Test
    public void testProjectConfig() {
        SonarToGerritPublisher p = new SonarToGerritPublisher();
        p.setProjectPath("fghdfh^%&$(&*IOUM V");
        SubJobConfig subJobConfig = p.getSubJobConfigs().get(0);
        Assert.assertEquals("fghdfh^%&$(&*IOUM V", subJobConfig.getProjectPath());

        subJobConfig.setProjectPath("fghdfh^%&$(&*IOUM V");
        Assert.assertEquals("fghdfh^%&$(&*IOUM V", subJobConfig.getProjectPath());
    }
}
