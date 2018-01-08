package org.jenkinsci.plugins.sonargerrit;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger;
import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.sonargerrit.config.*;
import org.jenkinsci.plugins.sonargerrit.filter.IssueFilter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarConnector;
import org.jenkinsci.plugins.sonargerrit.integration.IssueAdapterProcessor;
import org.jenkinsci.plugins.sonargerrit.review.GerritConnectionInfo;
import org.jenkinsci.plugins.sonargerrit.review.GerritConnector;
import org.jenkinsci.plugins.sonargerrit.review.GerritReviewBuilder;
import org.jenkinsci.plugins.sonargerrit.review.GerritRevisionWrapper;
import org.jenkinsci.plugins.sonargerrit.util.Localization;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */
public class SonarToGerritPublisher extends Publisher implements SimpleBuildStep {

    private static final Logger LOGGER = Logger.getLogger(SonarToGerritPublisher.class.getName());
    public static final String GERRIT_CHANGE_NUMBER_ENV_VAR_NAME = "GERRIT_CHANGE_NUMBER";
    public static final String GERRIT_NAME_ENV_VAR_NAME = "GERRIT_NAME";
    public static final String GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME = "GERRIT_PATCHSET_NUMBER";

    // ------------------ configuration settings
    /*
    * The URL of SonarQube server to be used for comments
    * */
    @Nonnull
    private InspectionConfig inspectionConfig = new InspectionConfig();

    @Nonnull
    private NotificationConfig notificationConfig = new NotificationConfig();

    @Nonnull
    private ReviewConfig reviewConfig = new ReviewConfig();

    private ScoreConfig scoreConfig = null;

    private GerritAuthenticationConfig authConfig = null;

    @DataBoundConstructor
    public SonarToGerritPublisher() {
    }

    //    @DataBoundConstructor
    @Deprecated //since 2.0. Left here for Jenkins version < 1.625.3
    public SonarToGerritPublisher(String sonarURL, List<SubJobConfig> subJobConfigs,
                                  String severity, boolean changedLinesOnly, boolean newIssuesOnly,
                                  String noIssuesToPostText, String someIssuesToPostText, String issueComment,
                                  boolean overrideCredentials, String httpUsername, String httpPassword,
                                  boolean postScore, String category, String noIssuesScore, String issuesScore,
                                  String noIssuesNotification, String issuesNotification) {
        setSonarURL(sonarURL);
        setSubJobConfigs(subJobConfigs);

        if (overrideCredentials) {
            setAuthConfig(new GerritAuthenticationConfig(httpUsername, httpPassword));
        }

        IssueFilterConfig issueFilterConfig = new IssueFilterConfig(severity, newIssuesOnly, changedLinesOnly, false);

        ReviewConfig reviewConfig = new ReviewConfig(issueFilterConfig, noIssuesToPostText, someIssuesToPostText, issueComment);
        setReviewConfig(reviewConfig);

        if (postScore) {
            ScoreConfig scoreConfig = new ScoreConfig(issueFilterConfig, category, Integer.parseInt(noIssuesScore), Integer.parseInt(issuesScore));
            setScoreConfig(scoreConfig);
        } else {
            setScoreConfig(null);
        }

        NotificationConfig notificationConfig = new NotificationConfig(noIssuesNotification, issuesNotification, issuesNotification);
        setNotificationConfig(notificationConfig);
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        //load inspection report
        SonarConnector sonarConnector = new SonarConnector(listener, inspectionConfig);
        sonarConnector.readSonarReports(filePath);

        //load revision info
        GerritTrigger trigger = GerritTrigger.getTrigger(run.getParent());
        Map<String, String> envVars = getEnvVars(run, listener, (String[]) GerritConnectionInfo.REQUIRED_VARS.toArray());
        GerritConnectionInfo connectionInfo = new GerritConnectionInfo(envVars, trigger, authConfig);
        try {
            GerritConnector connector = new GerritConnector(connectionInfo);
            connector.connect();
            GerritRevisionWrapper revisionInfo = new GerritRevisionWrapper(connector.getRevision());
            revisionInfo.loadData();

            Map<String, Set<Integer>> fileToChangedLines = revisionInfo.getFileToChangedLines();

            //match inspection report and revision info
            if (inspectionConfig.isPathCorrectionNeeded()) {
                new IssueAdapterProcessor(listener, sonarConnector, revisionInfo).process();
            }

            //generate review output
            //get issues to be commented
            Multimap<String, IssueAdapter> file2issuesToComment = getFilteredFileToIssueMultimap(
                    reviewConfig.getIssueFilterConfig(), sonarConnector, fileToChangedLines);
            TaskListenerLogger.logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.issues.to.comment", file2issuesToComment.entries().size());

            //get issues to be scored
            Multimap<String, IssueAdapter> file2issuesToScore = null;
            boolean postScore = scoreConfig != null;
            if (postScore) {
                file2issuesToScore = getFilteredFileToIssueMultimap(
                        scoreConfig.getIssueFilterConfig(), sonarConnector, fileToChangedLines);
                TaskListenerLogger.logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.issues.to.score", file2issuesToScore.entries().size());
            }

            boolean hasIssues = (!file2issuesToComment.isEmpty()) || (file2issuesToScore != null && !file2issuesToScore.isEmpty());
            if (!hasIssues && reviewConfig.getIssueFilterConfig().getFailOnly()) {
                return;
            }

            //send review
            ReviewInput reviewInput = new GerritReviewBuilder(file2issuesToComment, file2issuesToScore,
                    reviewConfig, scoreConfig, notificationConfig, inspectionConfig
            ).buildReview();
            revisionInfo.sendReview(reviewInput);

            TaskListenerLogger.logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.review.sent");
        } catch (RestApiException e) {
            LOGGER.log(Level.SEVERE, "Unable to post review: " + e.getMessage(), e);
            throw new AbortException("Unable to post review: " + e.getMessage());
        } catch (NullPointerException | IllegalArgumentException | IllegalStateException e) {
            throw new AbortException(e.getMessage());
        }
    }

    private Multimap<String, IssueAdapter> getFilteredFileToIssueMultimap(IssueFilterConfig filterConfig,
                                                                          SonarConnector sonarConnector,
                                                                          Map<String, Set<Integer>> fileToChangedLines) {
        IssueFilter commentFilter = new IssueFilter(filterConfig, sonarConnector.getIssues(), fileToChangedLines);
        Iterable<IssueAdapter> issuesToComment = commentFilter.filter();
        return sonarConnector.getReportData(issuesToComment);
    }

    private Map<String, String> getEnvVars(Run<?, ?> run, TaskListener listener, String... varNames) throws IOException, InterruptedException {
        Map<String, String> envVars = new HashMap<>();
        for (String varName : varNames) {
            envVars.put(varName, getEnvVar(run, listener, varName));
        }
        return envVars;
    }

    private String getEnvVar(Run<?, ?> run, TaskListener listener, String name) throws IOException, InterruptedException {
        EnvVars envVars = run.getEnvironment(listener);
        String value = envVars.get(name);
        // due to JENKINS-30910 old versions of workflow-job-plugin do not have code copying ParameterAction values to Environment Variables in pipeline jobs.
        if (value == null) {
            ParametersAction action = run.getAction(ParametersAction.class);
            if (action != null) {
                ParameterValue parameter = action.getParameter(name);
                if (parameter != null) {
                    Object parameterValue = parameter.getValue();
                    if (parameterValue != null) {
                        value = parameterValue.toString();
                    }
                }
            }
        }
        return value;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }


    /**
     * Descriptor for {@link SonarToGerritPublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p>
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/SonarToGerritBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Symbol("sonarToGerrit")
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public static final NotifyHandling NOTIFICATION_RECIPIENT_NO_ISSUES = NotifyHandling.NONE;
        public static final NotifyHandling NOTIFICATION_RECIPIENT_SOME_ISSUES = NotifyHandling.OWNER;
        public static final NotifyHandling NOTIFICATION_RECIPIENT_NEGATIVE_SCORE = NotifyHandling.OWNER;

        public static final String PROJECT_PATH = "";
        public static final String SONAR_REPORT_PATH = "target/sonar/sonar-report.json";
        public static final String SONAR_URL = "http://localhost:9000";
        public static final String DEFAULT_INSPECTION_CONFIG_TYPE = InspectionConfig.DescriptorImpl.BASE_TYPE;
        public static final boolean AUTO_MATCH_INSPECTION_AND_REVISION_PATHS = false;

        public static final String NO_ISSUES_TEXT = Localization.getLocalized("jenkins.plugin.default.review.title.no.issues");
        public static final String SOME_ISSUES_TEXT = Localization.getLocalized("jenkins.plugin.default.review.title.issues");
        public static final String ISSUE_COMMENT_TEXT = Localization.getLocalized("jenkins.plugin.default.review.body");
        public static final boolean FAIL_ONLY = false;

        public static final String CATEGORY = "Code-Review";
        public static final Integer NO_ISSUES_SCORE = 1;
        public static final Integer SOME_ISSUES_SCORE = -1;

        public static final boolean OVERRIDE_CREDENTIALS = false;

        public static final String SEVERITY = Severity.INFO.name();
        public static final boolean NEW_ISSUES_ONLY = false;
        public static final boolean CHANGED_LINES_ONLY = false;

        public static final int DEFAULT_SCORE = 0;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return getLocalized("jenkins.plugin.build.step.name");
        }

    }

    @Nonnull
    public InspectionConfig getInspectionConfig() {
        return inspectionConfig;
    }

    @Nonnull
    public NotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    @Nonnull
    public ReviewConfig getReviewConfig() {
        return reviewConfig;
    }

    public ScoreConfig getScoreConfig() {
        return scoreConfig;
    }

    public GerritAuthenticationConfig getAuthConfig() {
        return authConfig;
    }

    @DataBoundSetter
    public void setInspectionConfig(@Nonnull InspectionConfig inspectionConfig) {
        this.inspectionConfig = MoreObjects.firstNonNull(inspectionConfig, new InspectionConfig());
    }

    @DataBoundSetter
    public void setNotificationConfig(@Nonnull NotificationConfig notificationConfig) {
        this.notificationConfig = MoreObjects.firstNonNull(notificationConfig, new NotificationConfig());
    }

    @DataBoundSetter
    public void setReviewConfig(@Nonnull ReviewConfig reviewConfig) {
        this.reviewConfig = MoreObjects.firstNonNull(reviewConfig, new ReviewConfig());
    }

    @DataBoundSetter
    public void setScoreConfig(ScoreConfig scoreConfig) {
        this.scoreConfig = scoreConfig;
    }

    @DataBoundSetter
    public void setAuthConfig(GerritAuthenticationConfig authConfig) {
        this.authConfig = authConfig;
    }

    // --------------deprecated methods to support back compatibility

    @Deprecated
    @DataBoundSetter
    public void setSonarURL(String sonarURL) {
        this.inspectionConfig.setServerURL(sonarURL);
    }

    @Deprecated
    @DataBoundSetter
    public void setSubJobConfigs(List<SubJobConfig> subJobConfigs) {
        if (subJobConfigs == null || subJobConfigs.size() == 0) {
            this.inspectionConfig.setBaseConfig(new SubJobConfig());
            this.inspectionConfig.setSubJobConfigs(new LinkedList<SubJobConfig>());
        } else if (subJobConfigs.size() == 1){
            this.inspectionConfig.setBaseConfig(subJobConfigs.get(0));
            this.inspectionConfig.setSubJobConfigs(new LinkedList<SubJobConfig>());
        } else {
            this.inspectionConfig.setBaseConfig(null);
            this.inspectionConfig.setSubJobConfigs(subJobConfigs);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setSeverity(String severity) {
        ReviewConfig reviewConfig = getReviewConfig();
        IssueFilterConfig reviewFilterConfig = reviewConfig.getIssueFilterConfig();
        reviewFilterConfig.setSeverity(severity);

        ScoreConfig scoreConfig = getScoreConfig();
        if (scoreConfig != null) {
            IssueFilterConfig scoreFilterConfig = scoreConfig.getIssueFilterConfig();
            scoreFilterConfig.setSeverity(severity);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setNewIssuesOnly(Boolean changedLinesOnly) {
        ReviewConfig reviewConfig = getReviewConfig();
        IssueFilterConfig reviewFilterConfig = reviewConfig.getIssueFilterConfig();
        reviewFilterConfig.setNewIssuesOnly(changedLinesOnly);

        ScoreConfig scoreConfig = getScoreConfig();
        if (scoreConfig != null) {
            IssueFilterConfig scoreFilterConfig = scoreConfig.getIssueFilterConfig();
            scoreFilterConfig.setNewIssuesOnly(changedLinesOnly);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setChangedLinesOnly(Boolean changedLinesOnly) {
        ReviewConfig reviewConfig = getReviewConfig();
        IssueFilterConfig reviewFilterConfig = reviewConfig.getIssueFilterConfig();
        reviewFilterConfig.setChangedLinesOnly(changedLinesOnly);

        ScoreConfig scoreConfig = getScoreConfig();
        if (scoreConfig != null) {
            IssueFilterConfig scoreFilterConfig = scoreConfig.getIssueFilterConfig();
            scoreFilterConfig.setChangedLinesOnly(changedLinesOnly);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setNoIssuesToPostText(String noIssuesToPost) {
        ReviewConfig reviewConfig = getReviewConfig();
        reviewConfig.setNoIssuesTitleTemplate(noIssuesToPost);
    }

    @Deprecated
    @DataBoundSetter
    public void setSomeIssuesToPostText(String someIssuesToPost) {
        ReviewConfig reviewConfig = getReviewConfig();
        reviewConfig.setSomeIssuesTitleTemplate(someIssuesToPost);
    }

    @Deprecated
    @DataBoundSetter
    public void setIssueComment(String issueComment) {
        ReviewConfig reviewConfig = getReviewConfig();
        reviewConfig.setIssueCommentTemplate(issueComment);
    }

    @Deprecated
    @DataBoundSetter
    public void setOverrideCredentials(Boolean overrideCredentials) {
        if (overrideCredentials) {
            if (authConfig == null) {
                authConfig = new GerritAuthenticationConfig();
            }
        } else {
            authConfig = null;
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setHttpUsername(String overrideHttpUsername) {
        if (authConfig != null) {
            authConfig.setUsername(overrideHttpUsername);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setHttpPassword(String overrideHttpPassword) {
        if (authConfig != null) {
            authConfig.setPassword(overrideHttpPassword);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setPostScore(Boolean postScore) {
        if (postScore) {
            if (scoreConfig == null) {
                scoreConfig = new ScoreConfig();
            }
        } else {
            scoreConfig = null;
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setCategory(String category) {
        if (scoreConfig != null) {
            scoreConfig.setCategory(category);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setNoIssuesScore(String score) {
        if (scoreConfig != null) {
            try {
                scoreConfig.setNoIssuesScore(Integer.parseInt(score));
            } catch (NumberFormatException nfe) {
                // keep default
            }
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setIssuesScore(String score) {
        if (scoreConfig != null) {
            try {
                scoreConfig.setIssuesScore(Integer.parseInt(score));
            } catch (NumberFormatException nfe) {
                // keep default
            }
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setNoIssuesNotification(String notification) {
        notificationConfig.setNoIssuesNotificationRecipient(notification);
    }

    @Deprecated
    @DataBoundSetter
    public void setIssuesNotification(String notification) {
        notificationConfig.setCommentedIssuesNotificationRecipient(notification);
    }

    @Deprecated
    @DataBoundSetter
    public void setProjectPath(String path) {
        if (inspectionConfig.getBaseConfig() == null){
            inspectionConfig.setBaseConfig(new SubJobConfig());
        }
        inspectionConfig.getBaseConfig().setProjectPath(path);
    }

    @Deprecated
    @DataBoundSetter
    public void setPath(String path) {
        if (inspectionConfig.getBaseConfig() == null){
            inspectionConfig.setBaseConfig(new SubJobConfig());
        }
        inspectionConfig.getBaseConfig().setSonarReportPath(path);
    }
}

