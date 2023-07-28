package org.jenkinsci.plugins.sonargerrit.util;

import com.google.common.base.MoreObjects;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.BooleanUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritAuthenticationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.Inspection;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 30.01.2018 13:04 */
@Restricted(NoExternalUse.class)
public final class BackCompatibilityHelper {
  private final SonarToGerritPublisher publisher;

  // optional properties to be populated if it is not yet known if they are needed
  private final ScoreConfig tempScoreConfig;
  private GerritAuthenticationConfig tempAuthConfig;

  public BackCompatibilityHelper(SonarToGerritPublisher publisher) {
    this.publisher = publisher;
    this.tempScoreConfig = new ScoreConfig();
    this.tempAuthConfig = new GerritAuthenticationConfig();
  }

  // set up Inspection Config
  public void setSonarURL(String sonarURL) {
    Inspection inspectionConfig = getOrCreateInspectionConfig();
    inspectionConfig.setServerURL(sonarURL);
  }

  public void setProjectPath(String path) {
    Inspection inspectionConfig = getOrCreateInspectionConfig();
    if (inspectionConfig.getBaseConfig() == null) {
      inspectionConfig.setBaseConfig(new SubJobConfig());
    }
    inspectionConfig.getBaseConfig().setProjectPath(path);
  }

  public void setPath(String path) {
    Inspection inspectionConfig = getOrCreateInspectionConfig();
    if (inspectionConfig.getBaseConfig() == null) {
      inspectionConfig.setBaseConfig(new SubJobConfig());
    }
    inspectionConfig.getBaseConfig().setSonarReportPath(path);
  }

  public void setSubJobConfigs(List<SubJobConfig> subJobConfigs) {
    Inspection inspectionConfig = getOrCreateInspectionConfig();
    if (subJobConfigs == null || subJobConfigs.isEmpty()) {
      inspectionConfig.setBaseConfig(new SubJobConfig());
      inspectionConfig.setSubJobConfigs(new LinkedList<>());
    } else if (subJobConfigs.size() == 1) {
      inspectionConfig.setBaseConfig(subJobConfigs.get(0));
      inspectionConfig.setSubJobConfigs(new LinkedList<>());
    } else {
      inspectionConfig.setBaseConfig(null);
      inspectionConfig.setSubJobConfigs(subJobConfigs);
    }
  }

  // set up Score Config
  public void setPostScore(Boolean postScore) {
    if (BooleanUtils.toBoolean(postScore)) {
      if (getScoreConfig() == null) {
        publisher.setScoreConfig(tempScoreConfig);
      }
    } else {
      publisher.setScoreConfig(null);
    }
  }

  public void setCategory(String category) {
    ScoreConfig scoreConfig = getOrCreateScoreConfig();
    scoreConfig.setCategory(category);
  }

  public void setNoIssuesScore(String score) {
    ScoreConfig scoreConfig = getOrCreateScoreConfig();
    try {
      scoreConfig.setNoIssuesScore(Integer.parseInt(score));
    } catch (NumberFormatException nfe) {
      // todo log : keep default
    }
  }

  public void setIssuesScore(String score) {
    ScoreConfig scoreConfig = getOrCreateScoreConfig();
    try {
      scoreConfig.setIssuesScore(Integer.parseInt(score));
    } catch (NumberFormatException nfe) {
      // todo log : keep default
    }
  }

  // set up filters for both Review Config and Score Config
  public void setSeverity(String severity) {
    ReviewConfig reviewConfig = getOrCreateReviewConfig();
    IssueFilterConfig reviewFilterConfig = reviewConfig.getIssueFilterConfig();
    reviewFilterConfig.setSeverity(severity);

    ScoreConfig scoreConfig = getOrCreateScoreConfig();
    IssueFilterConfig scoreFilterConfig = scoreConfig.getIssueFilterConfig();
    scoreFilterConfig.setSeverity(severity);
  }

  public void setNewIssuesOnly(Boolean changedLinesOnly) {
    ReviewConfig reviewConfig = getOrCreateReviewConfig();
    IssueFilterConfig reviewFilterConfig = reviewConfig.getIssueFilterConfig();
    reviewFilterConfig.setNewIssuesOnly(changedLinesOnly);

    ScoreConfig scoreConfig = getOrCreateScoreConfig();
    IssueFilterConfig scoreFilterConfig = scoreConfig.getIssueFilterConfig();
    scoreFilterConfig.setNewIssuesOnly(changedLinesOnly);
  }

  public void setChangedLinesOnly(Boolean changedLinesOnly) {
    ReviewConfig reviewConfig = getOrCreateReviewConfig();
    IssueFilterConfig reviewFilterConfig = reviewConfig.getIssueFilterConfig();
    reviewFilterConfig.setChangedLinesOnly(changedLinesOnly);

    ScoreConfig scoreConfig = getOrCreateScoreConfig();
    IssueFilterConfig scoreFilterConfig = scoreConfig.getIssueFilterConfig();
    scoreFilterConfig.setChangedLinesOnly(changedLinesOnly);
  }

  // set up Review Config

  public void setNoIssuesToPostText(String noIssuesToPost) {
    ReviewConfig reviewConfig = getOrCreateReviewConfig();
    reviewConfig.setNoIssuesTitleTemplate(noIssuesToPost);
  }

  public void setSomeIssuesToPostText(String someIssuesToPost) {
    ReviewConfig reviewConfig = getOrCreateReviewConfig();
    reviewConfig.setSomeIssuesTitleTemplate(someIssuesToPost);
  }

  public void setIssueComment(String issueComment) {
    ReviewConfig reviewConfig = getOrCreateReviewConfig();
    reviewConfig.setIssueCommentTemplate(issueComment);
  }

  public void setOmitDuplicateComments(boolean omitDuplicateComments) {
    ReviewConfig reviewConfig = getOrCreateReviewConfig();
    reviewConfig.setOmitDuplicateComments(omitDuplicateComments);
  }

  // set up Authentication Context

  public void setOverrideCredentials(Boolean overrideCredentials) {
    if (BooleanUtils.toBoolean(overrideCredentials)) {
      if (publisher.getAuthConfig() == null) {
        publisher.setAuthConfig(tempAuthConfig);
      }
    } else {
      publisher.setAuthConfig(null);
    }
  }

  public void setHttpUsername(String httpUsername) {
    alterAuthenticationConfig(
        config -> {
          config.setUsername(httpUsername);
          return config;
        });
  }

  public void setHttpPassword(String httpPassword) {
    alterAuthenticationConfig(
        config -> {
          config.setPassword(httpPassword);
          return config;
        });
  }

  // set up Notification Config

  public void setNoIssuesNotification(String notification) {
    NotificationConfig notificationConfig = getOrCreateNotificationConfig();
    notificationConfig.setNoIssuesNotificationRecipient(notification);
  }

  public void setIssuesNotification(String notification) {
    NotificationConfig notificationConfig = getOrCreateNotificationConfig();
    notificationConfig.setCommentedIssuesNotificationRecipient(notification);
  }

  // helper methods
  // mandatory properties - should be created anyway

  private Inspection getOrCreateInspectionConfig() {
    return getInspectionConfig();
  }

  private ReviewConfig getOrCreateReviewConfig() {
    return getReviewConfig();
  }

  private NotificationConfig getOrCreateNotificationConfig() {
    return getNotificationConfig();
  }

  /*
   * Use temporary variables for optional properties so their parameters to be saved if passed before the flag that creates them
   * */
  ScoreConfig getOrCreateScoreConfig() {
    return MoreObjects.firstNonNull(getScoreConfig(), tempScoreConfig);
  }

  private void alterAuthenticationConfig(UnaryOperator<GerritAuthenticationConfig> mutator) {
    boolean tempConfig = false;
    GerritAuthenticationConfig oldConfig = publisher.getAuthConfig();
    if (oldConfig == null) {
      tempConfig = true;
      oldConfig = tempAuthConfig;
    }
    GerritAuthenticationConfig newConfig = mutator.apply(oldConfig);
    if (tempConfig) {
      tempAuthConfig = newConfig;
    } else {
      publisher.setAuthConfig(newConfig);
    }
  }

  // getters returning null - support for pipeline snippet generator

  public String getSonarURL() {
    return null;
  }

  public Collection<SubJobConfig> getSubJobConfigs() {
    return null;
  }

  public String getSeverity() {
    return null;
  }

  public boolean isNewIssuesOnly() {
    return false;
  }

  public boolean isOmitDuplicateComments() {
    return false;
  }

  public boolean isChangedLinesOnly() {
    return false;
  }

  public String getNoIssuesToPostText() {
    return null;
  }

  public String getSomeIssuesToPostText() {
    return null;
  }

  public String getIssueComment() {
    return null;
  }

  public boolean isOverrideCredentials() {
    return false;
  }

  public String getHttpUsername() {
    return null;
  }

  public String getHttpPassword() {
    return null;
  }

  public boolean isPostScore() {
    return false;
  }

  public String getCategory() {
    return null;
  }

  public String getNoIssuesScore() {
    return null;
  }

  public String getIssuesScore() {
    return null;
  }

  public String getNoIssuesNotification() {
    return null;
  }

  public String getIssuesNotification() {
    return null;
  }

  public String getProjectPath() {
    return null;
  }

  public String getPath() {
    return null;
  }

  // simple getters

  private Inspection getInspectionConfig() {
    return publisher.getInspectionConfig();
  }

  private ReviewConfig getReviewConfig() {
    return publisher.getReviewConfig();
  }

  private ScoreConfig getScoreConfig() {
    return publisher.getScoreConfig();
  }

  private NotificationConfig getNotificationConfig() {
    return publisher.getNotificationConfig();
  }
}
