package org.jenkinsci.plugins.sonargerrit.gerrit;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.util.Date;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 18.11.2017 12:41 $Id$ */
@EnableJenkinsRule
public class ReviewResultTest {
  protected Multimap<String, Issue> scoreIssues = LinkedListMultimap.create();
  protected Multimap<String, Issue> commentIssues = LinkedListMultimap.create();
  protected SonarToGerritPublisher publisher;

  @BeforeEach
  public final void initialize() {
    doInitialize();
  }

  protected void doInitialize() {
    publisher = buildPublisher();
  }

  @Test
  public void testNoScoreConfig() {
    publisher.setScoreConfig(null);
    ReviewInput reviewResult = getReviewResult();
    Assertions.assertNull(reviewResult.labels);
  }

  protected ReviewInput getReviewResult() {
    GerritReviewBuilder builder =
        new GerritReviewBuilder(
            commentIssues,
            scoreIssues,
            publisher.getReviewConfig(),
            publisher.getScoreConfig(),
            publisher.getNotificationConfig());
    return builder.buildReview();
  }

  protected ReviewConfig getReviewConfig() {
    return publisher.getReviewConfig();
  }

  public static class DummyIssue implements Issue {
    @Override
    public Severity getSeverity() {
      return Severity.CRITICAL;
    }

    @Override
    public String getRule() {
      return "rule";
    }

    @Override
    public String getRuleLink() {
      return getRule();
    }

    @Override
    public String getStatus() {
      return null;
    }

    @Override
    public boolean isNew() {
      return false;
    }

    @Override
    public Date getCreationDate() {
      return null;
    }

    @Override
    public String getMessage() {
      return "message";
    }

    @Override
    public String getFilepath() {
      return getComponent();
    }

    @Override
    public String getKey() {
      return null;
    }

    @Override
    public String getComponent() {
      return null;
    }

    @Override
    public Integer getLine() {
      return null;
    }
  }

  protected SonarToGerritPublisher buildPublisher() {
    SonarToGerritPublisher publisher = new SonarToGerritPublisher();
    publisher.setScoreConfig(new ScoreConfig());
    return publisher;
  }
}
