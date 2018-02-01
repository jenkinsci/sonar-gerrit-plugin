package org.jenkinsci.plugins.sonargerrit.util;

import com.google.common.base.MoreObjects;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.config.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 30.01.2018 13:04
 */
public final class BackCompatibilityHelper {
    private SonarToGerritPublisher publisher;

    // optional properties to be populated if it is not yet known if they are needed
    private ScoreConfig tempScoreConfig;
    private GerritAuthenticationConfig tempAuthConfig;

    public BackCompatibilityHelper(SonarToGerritPublisher publisher) {
        this.publisher = publisher;
        this.tempScoreConfig = new ScoreConfig();
        this.tempAuthConfig = new GerritAuthenticationConfig();
    }

    // set up Inspection Config
    public void setSonarURL(String sonarURL) {
        InspectionConfig inspectionConfig = getOrCreateInspectionConfig();
        inspectionConfig.setServerURL(sonarURL);
    }

    public void setProjectPath(String path) {
        InspectionConfig inspectionConfig = getOrCreateInspectionConfig();
        if (inspectionConfig.getBaseConfig() == null) {
            inspectionConfig.setBaseConfig(new SubJobConfig());
        }
        inspectionConfig.getBaseConfig().setProjectPath(path);
    }

    public void setPath(String path) {
        InspectionConfig inspectionConfig = getOrCreateInspectionConfig();
        if (inspectionConfig.getBaseConfig() == null) {
            inspectionConfig.setBaseConfig(new SubJobConfig());
        }
        inspectionConfig.getBaseConfig().setSonarReportPath(path);
    }

    public void setSubJobConfigs(List<SubJobConfig> subJobConfigs) {
        InspectionConfig inspectionConfig = getOrCreateInspectionConfig();
        if (subJobConfigs == null || subJobConfigs.size() == 0) {
            inspectionConfig.setBaseConfig(new SubJobConfig());
            inspectionConfig.setSubJobConfigs(new LinkedList<SubJobConfig>());
        } else if (subJobConfigs.size() == 1) {
            inspectionConfig.setBaseConfig(subJobConfigs.get(0));
            inspectionConfig.setSubJobConfigs(new LinkedList<SubJobConfig>());
        } else {
            inspectionConfig.setBaseConfig(null);
            inspectionConfig.setSubJobConfigs(subJobConfigs);
        }
    }

    // set up Score Config
    public void setPostScore(Boolean postScore) {
        if (postScore) {
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

    // set up Authentication Context

    public void setOverrideCredentials(Boolean overrideCredentials) {
        if (overrideCredentials) {
            if (getAuthConfig() == null) {
                publisher.setAuthConfig(tempAuthConfig);
            }
        } else {
            publisher.setAuthConfig(null);
        }
    }

    public void setHttpUsername(String overrideHttpUsername) {
        AuthenticationConfig config = getOrCreateAuthenticationConfig();
        config.setUsername(overrideHttpUsername);
    }

    public void setHttpPassword(String overrideHttpPassword) {
        AuthenticationConfig config = getOrCreateAuthenticationConfig();
        config.setPassword(overrideHttpPassword);
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

    private InspectionConfig getOrCreateInspectionConfig() {
        if (getInspectionConfig() == null) {
            publisher.setInspectionConfig(new InspectionConfig());
        }
        return getInspectionConfig();
    }

    private ReviewConfig getOrCreateReviewConfig() {
        if (getReviewConfig() == null) {
            publisher.setReviewConfig(new ReviewConfig());
        }
        return getReviewConfig();
    }

    private NotificationConfig getOrCreateNotificationConfig() {
        if (getNotificationConfig() == null) {
            publisher.setNotificationConfig(new NotificationConfig());
        }
        return getNotificationConfig();
    }

    /* 
    * Use temporary variables for optional properties so their parameters to be saved if passed before the flag that creates them
    * */
    protected ScoreConfig getOrCreateScoreConfig() {
        return MoreObjects.firstNonNull(getScoreConfig(), tempScoreConfig);
    }


    private GerritAuthenticationConfig getOrCreateAuthenticationConfig() {
        return MoreObjects.firstNonNull(getAuthConfig(), tempAuthConfig);
    }

    // getters returning null - support for pipeline snippet generator

    public String getSonarURL() {
        return getNull(String.class);

    }


    public Collection<SubJobConfig> getSubJobConfigs() {
        return getNull(Collection.class);
    }


    public String getSeverity() {
        return getNull(String.class);
    }


    public boolean isNewIssuesOnly() {
        return getNull();
    }


    public boolean isChangedLinesOnly() {
        return getNull();
    }


    public String getNoIssuesToPostText() {
        return getNull(String.class);
    }


    public String getSomeIssuesToPostText() {
        return getNull(String.class);
    }


    public String getIssueComment() {
        return getNull(String.class);
    }


    public boolean isOverrideCredentials() {
        return getNull();
    }


    public String getHttpUsername() {
        return getNull(String.class);
    }


    public String getHttpPassword() {
        return getNull(String.class);
    }


    public boolean isPostScore() {
        return getNull();
    }


    public String getCategory() {
        return getNull(String.class);
    }


    public String getNoIssuesScore() {
        return getNull(String.class);
    }


    public String getIssuesScore() {
        return getNull(String.class);
    }


    public String getNoIssuesNotification() {
        return getNull(String.class);
    }


    public String getIssuesNotification() {
        return getNull(String.class);
    }


    public String getProjectPath() {
        return getNull(String.class);
    }

    public String getPath() {
        return getNull(String.class);
    }

    private <V extends Object> V getNull(Class<V> clazz) {
        return null;
    }

    private boolean getNull() {
        return false;
    }

    // simple getters

    private InspectionConfig getInspectionConfig() {
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

    private GerritAuthenticationConfig getAuthConfig() {
        return publisher.getAuthConfig();
    }


}
