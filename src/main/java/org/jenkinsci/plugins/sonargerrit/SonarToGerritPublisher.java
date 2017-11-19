package org.jenkinsci.plugins.sonargerrit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritManagement;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.IGerritHudsonTriggerConfig;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.sonargerrit.config.*;
import org.jenkinsci.plugins.sonargerrit.data.ComponentPathBuilder;
import org.jenkinsci.plugins.sonargerrit.data.SonarReportBuilder;
import org.jenkinsci.plugins.sonargerrit.data.converter.CustomIssueFormatter;
import org.jenkinsci.plugins.sonargerrit.data.converter.CustomReportFormatter;
import org.jenkinsci.plugins.sonargerrit.data.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.data.entity.Report;
import org.jenkinsci.plugins.sonargerrit.data.entity.Severity;
import org.jenkinsci.plugins.sonargerrit.data.predicates.ByMinSeverityPredicate;
import org.jenkinsci.plugins.sonargerrit.data.predicates.ByNewPredicate;
import org.jenkinsci.plugins.sonargerrit.util.Localization;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    private String sonarURL = DescriptorImpl.SONAR_URL;

    @Nonnull
    private List<SubJobConfig> subJobConfigs = DescriptorImpl.JOB_CONFIGS;

    @Nonnull
    private NotificationConfig notificationConfig = new NotificationConfig(
            DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES,
            DescriptorImpl.NOTIFICATION_RECIPIENT_SOME_ISSUES,
            DescriptorImpl.NOTIFICATION_RECIPIENT_NEGATIVE_SCORE);

    @Nonnull
    private ReviewConfig reviewConfig = new ReviewConfig(
            new IssueFilterConfig(
                    DescriptorImpl.SEVERITY,
                    DescriptorImpl.NEW_ISSUES_ONLY,
                    DescriptorImpl.CHANGED_LINES_ONLY),
            DescriptorImpl.NO_ISSUES_TEXT,
            DescriptorImpl.SOME_ISSUES_TEXT,
            DescriptorImpl.ISSUE_COMMENT_TEXT);

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

        IssueFilterConfig issueFilterConfig = new IssueFilterConfig(severity, newIssuesOnly, changedLinesOnly);

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


    @VisibleForTesting
    static Multimap<String, Issue> generateFilenameToIssuesMapFilteredByPredicates(String projectPath, Report report, Iterable<Issue> filtered) {
        final Multimap<String, Issue> file2issues = LinkedListMultimap.create();
        // generating map consisting of real file names to corresponding issues
        // collections.
        final ComponentPathBuilder pathBuilder = new ComponentPathBuilder(report.getComponents());
        for (Issue issue : filtered) {
            String issueComponent = issue.getComponent();
            String realFileName = pathBuilder.buildPrefixedPathForComponentWithKey(issueComponent, projectPath)
                    .or(issueComponent);
            file2issues.put(realFileName, issue);
        }
        return file2issues;
    }

    private boolean postScore() {
        return this.scoreConfig != null;
    }


    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        List<ReportInfo> issueInfos = readSonarReports(listener, filePath);
        if (issueInfos == null) {        //todo make more readable
            throw new AbortException(getLocalized("jenkins.plugin.error.path.no.project.config.available"));
        }

        IssueFilterConfig reviewIssueFilterConfig = reviewConfig.getIssueFilterConfig();
        Multimap<String, Issue> file2issuesToComment = generateFilenameToIssuesMapFilteredByPredicates(issueInfos, reviewIssueFilterConfig);

        IssueFilterConfig scoreIssueFilterConfig = scoreConfig.getIssueFilterConfig();
        Multimap<String, Issue> file2issuesToScore = postScore() ? generateFilenameToIssuesMapFilteredByPredicates(issueInfos, scoreIssueFilterConfig) : null;

        // Step 3 - Prepare Gerrit REST API client
        // Check Gerrit configuration is available
        String gerritNameEnvVar = getEnvVar(run, listener, GERRIT_NAME_ENV_VAR_NAME);
        GerritTrigger trigger = GerritTrigger.getTrigger(run.getParent());
        String gerritServerName = gerritNameEnvVar != null ? gerritNameEnvVar : trigger != null ? trigger.getServerName() : null;
        if (gerritServerName == null) {
            throw new AbortException(getLocalized("jenkins.plugin.error.gerrit.server.empty"));
        }
        IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(gerritServerName);
        if (gerritConfig == null) {
            throw new AbortException(getLocalized("jenkins.plugin.error.gerrit.config.empty"));
        }

        if (!gerritConfig.isUseRestApi()) {
            throw new AbortException(getLocalized("jenkins.plugin.error.gerrit.restapi.off"));
        }
        if (gerritConfig.getGerritHttpUserName() == null) {
            throw new AbortException(getLocalized("jenkins.plugin.error.gerrit.user.empty"));
        }
        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        String username = authConfig == null ? gerritConfig.getGerritHttpUserName() : authConfig.getUsername();
        String password = authConfig == null ? gerritConfig.getGerritHttpPassword() : authConfig.getPassword();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(gerritConfig.getGerritFrontEndUrl(),
                username, password, gerritConfig.isUseRestApi());
        GerritApi gerritApi = gerritRestApiFactory.create(authData);
        try {
            String changeNStr = getEnvVar(run, listener, GERRIT_CHANGE_NUMBER_ENV_VAR_NAME);
            if (changeNStr == null) {
                throw new AbortException(getLocalized("jenkins.plugin.error.gerrit.change.number.empty"));
            }
            int changeNumber = Integer.parseInt(changeNStr);

            String patchsetNStr = getEnvVar(run, listener, GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME);
            if (patchsetNStr == null) {
                throw new AbortException(getLocalized("jenkins.plugin.error.gerrit.patchset.number.empty"));
            }
            int patchSetNumber = Integer.parseInt(patchsetNStr);

            RevisionApi revision = gerritApi.changes().id(changeNumber).revision(patchSetNumber);
            logMessage(listener, "jenkins.plugin.connected.to.gerrit", Level.INFO, new Object[]{gerritServerName, changeNumber, patchSetNumber});

            // Step 4 - Filter issues by changed files
            file2issuesToComment = filterIssuesByChangedFiles(file2issuesToComment, revision);

//            logResultMap(file2issuesToComment, "Filter issues by changed files: {0} elements");

            if (reviewIssueFilterConfig.isChangedLinesOnly()) {
                // Step 4a - Filter issues by changed lines in file only
                filterIssuesByChangedLines(file2issuesToComment, revision);
//                logResultMap(file2issuesToComment, "Filter issues by changed lines: {0} elements");
            }

            if (postScore() && scoreIssueFilterConfig.isChangedLinesOnly()) {
                // Step 4a - Filter issues by changed lines in file only
                filterIssuesByChangedLines(file2issuesToScore, revision);
//                logResultMap(file2issuesToComment, "Filter issues by changed lines: {0} elements");
            }

            // Step 6 - Send review to Gerrit
            ReviewInput reviewInput = getReviewResult(file2issuesToComment, file2issuesToScore);

            // Step 7 - Post review
            revision.review(reviewInput);
            logMessage(listener, "jenkins.plugin.review.sent", Level.INFO);
        } catch (RestApiException e) {
            LOGGER.log(Level.SEVERE, "Unable to post review: " + e.getMessage(), e);
            throw new AbortException("Unable to post review: " + e.getMessage());
        }
    }

    private Multimap<String, Issue> filterIssuesByChangedFiles(Multimap<String, Issue> file2issues, RevisionApi revision) throws RestApiException {
        final Map<String, FileInfo> files = revision.files();
        file2issues = Multimaps.filterKeys(file2issues, new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return input != null && files.keySet().contains(input);
            }
        });
        return file2issues;
    }

    @VisibleForTesting
    Multimap<String, Issue> generateFilenameToIssuesMapFilteredByPredicates(List<ReportInfo> issueInfos, IssueFilterConfig filter) {
        Multimap<String, Issue> file2issues = LinkedListMultimap.create();
        for (ReportInfo info : issueInfos) {

            Report report = info.report;

            // Step 1 - Filter issues by issues only predicates
            Iterable<Issue> filtered = filterIssuesByPredicates(report.getIssues(), filter);

            // Step 2 - Calculate real file name for issues and store to multimap
            file2issues.putAll(generateFilenameToIssuesMapFilteredByPredicates(info.directoryPath, report, filtered));
        }
        return file2issues;
    }

    private Report readSonarReport(TaskListener listener, FilePath workspace, SubJobConfig config) throws IOException,
            InterruptedException {
        FilePath reportPath = workspace.child(config.getSonarReportPath());
        if (!reportPath.exists()) {
            logMessage(listener, "jenkins.plugin.error.sonar.report.not.exists", Level.SEVERE, reportPath);
            return null;
        }

        if (reportPath.isDirectory()) {
            logMessage(listener, "jenkins.plugin.error.sonar.report.path.directory", Level.SEVERE, reportPath);
            return null;
        }
        logMessage(listener, "jenkins.plugin.getting.report", Level.INFO, reportPath);

        SonarReportBuilder builder = new SonarReportBuilder();
        String reportJson = reportPath.readToString();
        Report report = builder.fromJson(reportJson);
        logMessage(listener, "jenkins.plugin.report.loaded", Level.INFO, report.getIssues().size());
        return report;
    }

    @VisibleForTesting
    List<ReportInfo> readSonarReports(TaskListener listener, FilePath workspace) throws IOException,
            InterruptedException {
        List<ReportInfo> reports = new ArrayList<ReportInfo>();
        for (SubJobConfig subJobConfig : getSubJobConfigs(false)) { // to be replaced by this.subJobConfigs in further releases - this code is to support older versions
            Report report = readSonarReport(listener, workspace, subJobConfig);
            if (report == null) {
                return null;
            }
            reports.add(new ReportInfo(subJobConfig.getProjectPath(), report));
        }
        return reports;
    }

    private String getEnvVar(Run<?, ?> build, TaskListener listener, String name) throws IOException, InterruptedException {
        EnvVars envVars = build.getEnvironment(listener);
        String value = envVars.get(name);
        // due to JENKINS-30910 old versions of workflow-job-plugin do not have code copying ParameterAction values to Environment Variables in pipeline jobs.
        if (value == null) {
            ParametersAction action = build.getAction(ParametersAction.class);
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

    private void logMessage(TaskListener listener, String message, Level l, Object... params) {
        message = getLocalized(message, params);
        if (listener != null) {     // it can be it tests
            listener.getLogger().println(message);
        }
        LOGGER.log(l, message);
    }

    private String getReviewMessage(Multimap<String, Issue> finalIssues) {
        return new CustomReportFormatter(finalIssues.values(), reviewConfig.getSomeIssuesTitleTemplate(), reviewConfig.getNoIssuesTitleTemplate()).getMessage();
    }

    private int getReviewMark(int finalIssuesCount) {
        Integer mark = finalIssuesCount > 0 ? scoreConfig.getIssuesScore() : scoreConfig.getNoIssuesScore();
        return mark.intValue();
    }

    public List<SubJobConfig> getSubJobConfigs() {
        return getSubJobConfigs(true);
    }

    public List<SubJobConfig> getSubJobConfigs(boolean addDefault) {
        if (subJobConfigs == null) {
            subJobConfigs = new ArrayList<SubJobConfig>();
            // add configuration from previous plugin version
            if (addDefault) {
                subJobConfigs.add(DescriptorImpl.JOB_CONFIG);
            }
        }
        return subJobConfigs;
    }

    //todo replace by enum
    private NotifyHandling getNotificationSettings(int finalIssuesToCommentCount, int score) {
        return score < 0 ?
                NotifyHandling.valueOf(notificationConfig.getNegativeScoreNotificationRecipient()) :
                finalIssuesToCommentCount > 0 ?
                        NotifyHandling.valueOf(notificationConfig.getCommentedIssuesNotificationRecipient()) :
                        NotifyHandling.valueOf(notificationConfig.getNoIssuesNotificationRecipient());
    }

//    private int parseNumber(String number, int deflt) {
//        try {
//            return Integer.parseInt(number);
//        } catch (NumberFormatException e) {
//            return deflt;
//        }
//
//    }

    @VisibleForTesting
    ReviewInput getReviewResult(Multimap<String, Issue> finalIssuesToComment, Multimap<String, Issue> finalIssuesToScore) {
        String reviewMessage = getReviewMessage(finalIssuesToComment);
        ReviewInput reviewInput = new ReviewInput().message(reviewMessage);


        int score = postScore() ? getReviewMark(finalIssuesToScore.size()) : 0;
        if (postScore()) {
            reviewInput.label(scoreConfig.getCategory(), score);
        }
        reviewInput.notify = getNotificationSettings(finalIssuesToComment.size(), score);

        reviewInput.comments = new HashMap<String, List<ReviewInput.CommentInput>>();
        for (String file : finalIssuesToComment.keySet()) {
            reviewInput.comments.put(file, Lists.newArrayList(
                            Collections2.transform(finalIssuesToComment.get(file),
                                    new Function<Issue, ReviewInput.CommentInput>() {
                                        @Nullable
                                        @Override
                                        public ReviewInput.CommentInput apply(@Nullable Issue input) {
                                            if (input == null) {
                                                return null;
                                            }
                                            ReviewInput.CommentInput commentInput = new ReviewInput.CommentInput();
                                            commentInput.id = input.getKey();
                                            commentInput.line = input.getLine();
                                            commentInput.message = new CustomIssueFormatter(input, reviewConfig.getIssueCommentTemplate(), getSonarURL()).getMessage();
                                            return commentInput;
                                        }

                                    }
                            )
                    )
            );
        }
        return reviewInput;
    }

    @VisibleForTesting
    void filterIssuesByChangedLines(Multimap<String, Issue> finalIssues, RevisionApi revision) throws RestApiException {
        for (String filename : new HashSet<String>(finalIssues.keySet())) {
            List<DiffInfo.ContentEntry> content = revision.file(filename).diff().content;
            int processed = 0;
//            final RangeSet<Integer> rangeSet = TreeRangeSet.create();
            Set<Integer> rangeSet = new HashSet<Integer>();
            for (DiffInfo.ContentEntry contentEntry : content) {
                if (contentEntry.ab != null) {
                    processed += contentEntry.ab.size();
                } else if (contentEntry.b != null) {
                    int start = processed + 1;
                    int end = processed + contentEntry.b.size();
                    for (int i = start; i <= end; i++) {    // todo use guava Range for this purpose
                        rangeSet.add(i);
                    }
//                    rangeSet.add(Range.closed(start, end));
                    processed += contentEntry.b.size();
                }
            }

            Collection<Issue> issues = new ArrayList<Issue>(finalIssues.get(filename));
            for (Issue i : issues) {
                if (!rangeSet.contains(i.getLine())) {
                    finalIssues.get(filename).remove(i);
                }
            }
        }
    }

    @VisibleForTesting
    Iterable<Issue> filterIssuesByPredicates(List<Issue> issues, IssueFilterConfig filter) {
        Severity sev = Severity.valueOf(filter.getSeverity());
        return Iterables.filter(issues,
                Predicates.and(
                        ByMinSeverityPredicate.apply(sev),   // if Info - extra work
                        ByNewPredicate.apply(filter.isNewIssuesOnly()))  // if false - extra work
        );
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

    @VisibleForTesting
    static class ReportInfo {

        private String directoryPath;
        private Report report;

        public ReportInfo(String directoryPath, Report report) {
            this.directoryPath = directoryPath;
            this.report = report;
        }

    }

    /**
     * Descriptor for {@link SonarToGerritPublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
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

        public static final String NO_ISSUES_TEXT = Localization.getLocalized("jenkins.plugin.default.review.title.no.issues");
        public static final String SOME_ISSUES_TEXT = Localization.getLocalized("jenkins.plugin.default.review.title.issues");
        public static final String ISSUE_COMMENT_TEXT = Localization.getLocalized("jenkins.plugin.default.review.body");

        public static final String CATEGORY = "Code-Review";
        public static final Integer NO_ISSUES_SCORE = 1;
        public static final Integer SOME_ISSUES_SCORE = -1;

        public static final boolean OVERRIDE_CREDENTIALS = false;

        public static final String SEVERITY = Severity.INFO.name();
        public static final boolean NEW_ISSUES_ONLY = false;
        public static final boolean CHANGED_LINES_ONLY = false;

        public static final SubJobConfig JOB_CONFIG = new SubJobConfig(PROJECT_PATH, SONAR_REPORT_PATH);
        public static final List<SubJobConfig> JOB_CONFIGS = new LinkedList<>(Arrays.asList(JOB_CONFIG));

//        public static final IssueFilterConfig COMMENT_ISSUE_FILTER = new IssueFilterConfig(SEVERITY, NEW_ISSUES_ONLY, CHANGED_LINES_ONLY);

//        public static final IssueFilterConfig SCORE_ISSUE_FILTER = new IssueFilterConfig(SEVERITY, NEW_ISSUES_ONLY, CHANGED_LINES_ONLY);

        public static final int DEFAULT_SCORE = 0;

//        public static final ReviewConfig REVIEW_CONFIG = new ReviewConfig(COMMENT_ISSUE_FILTER, NO_ISSUES_TEXT, SOME_ISSUES_TEXT, ISSUE_COMMENT_TEXT);

//        public static final NotificationConfig NOTIFICATION_CONFIG = new NotificationConfig(NOTIFICATION_RECIPIENT_NO_ISSUES, NOTIFICATION_RECIPIENT_SOME_ISSUES, NOTIFICATION_RECIPIENT_NEGATIVE_SCORE);

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'sonarURL'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p/>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckSonarURL(@QueryParameter String value) throws ServletException, IOException {
            if (Util.fixEmptyAndTrim(value) == null) {
                return FormValidation.warning(getLocalized("jenkins.plugin.error.sonar.url.empty"));
            }
            try {
                new URL(value);
            } catch (MalformedURLException e) {
                return FormValidation.warning(getLocalized("jenkins.plugin.error.sonar.url.invalid"));
            }
            return FormValidation.ok();
        }

        //todo validate subconfigs?

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

    public String getSonarURL() {
        return sonarURL;
    }

    @Nonnull
    public NotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    //@SuppressWarnings(value = "unused")
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
    public void setSonarURL(String sonarURL) {
        this.sonarURL = MoreObjects.firstNonNull(sonarURL, DescriptorImpl.SONAR_URL);
    }

    @DataBoundSetter
    public void setSubJobConfigs(List<SubJobConfig> subJobConfigs) { // todo check sjc pats != null
        this.subJobConfigs = MoreObjects.firstNonNull(subJobConfigs, DescriptorImpl.JOB_CONFIGS);
    }

    @DataBoundSetter
    public void setNotificationConfig(@Nonnull NotificationConfig notificationConfig) {
        this.notificationConfig = notificationConfig;
    }

    @DataBoundSetter
    public void setReviewConfig(@Nonnull ReviewConfig reviewConfig) {
        this.reviewConfig = reviewConfig;
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
        if (subJobConfigs == null || subJobConfigs.isEmpty() || subJobConfigs == DescriptorImpl.JOB_CONFIGS) {
            subJobConfigs = new LinkedList<>();
            SubJobConfig config = new SubJobConfig(path, null);
            subJobConfigs.add(config);
        } else if (subJobConfigs.size() == 1 && subJobConfigs.get(0).getProjectPath() == null) {
            SubJobConfig config = subJobConfigs.get(0);
            config.setProjectPath(path);
        }
    }

    @Deprecated
    @DataBoundSetter
    public void setPath(String path) {
        if (subJobConfigs == null || subJobConfigs.isEmpty() || subJobConfigs == DescriptorImpl.JOB_CONFIGS) {
            subJobConfigs = new LinkedList<>();
            SubJobConfig config = new SubJobConfig(null, path);
            subJobConfigs.add(config);
        } else if (subJobConfigs.size() == 1 && subJobConfigs.get(0).getProjectPath() == null) {
            SubJobConfig config = subJobConfigs.get(0);
            config.setSonarReportPath(path);
        }
    }

}

