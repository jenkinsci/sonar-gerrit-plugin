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
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer;
import com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl;
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

    @Nonnull
    private String sonarURL = DescriptorImpl.SONAR_URL;
    @Nonnull
    private List<SubJobConfig> subJobConfigs = new ArrayList<>(DescriptorImpl.JOB_CONFIGS);
    @Nonnull
    private String severity = DescriptorImpl.SEVERITY;
    private boolean changedLinesOnly = DescriptorImpl.CHANGED_LINES_ONLY;
    private boolean newIssuesOnly = DescriptorImpl.NEW_ISSUES_ONLY;
    @Nonnull
    private String noIssuesToPostText = DescriptorImpl.NO_ISSUES_TEXT;
    @Nonnull
    private String someIssuesToPostText = DescriptorImpl.SOME_ISSUES_TEXT;
    @Nonnull
    private String issueComment = DescriptorImpl.ISSUE_COMMENT_TEXT;
    private boolean overrideCredentials = DescriptorImpl.OVERRIDE_CREDENTIALS;
    @Nonnull
    private String httpUsername;
    @Nonnull
    private String httpPassword;
    private boolean postScore = DescriptorImpl.POST_SCORE;
    @Nonnull
    private String category = DescriptorImpl.CATEGORY;
    @Nonnull
    private String noIssuesScore = DescriptorImpl.NO_ISSUES_SCORE;
    @Nonnull
    private String issuesScore = DescriptorImpl.SOME_ISSUES_SCORE;

    @Nonnull
    private String noIssuesNotification = DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES_STR;
    @Nonnull
    private String issuesNotification = DescriptorImpl.NOTIFICATION_RECIPIENT_SOME_ISSUES_STR;

    // to be removed
    private final String path;
    private final String projectPath;

    @DataBoundConstructor
    public SonarToGerritPublisher() {
        // old values - not used anymore. will be deleted in further releases
        this.path = null;
        this.projectPath = null;
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
        setSeverity(severity);
        setChangedLinesOnly(changedLinesOnly);
        setNewIssuesOnly(newIssuesOnly);
        setNoIssuesToPostText(noIssuesToPostText);
        setSomeIssuesToPostText(someIssuesToPostText);
        setIssueComment(issueComment);
        setOverrideCredentials(overrideCredentials);
        setHttpUsername(httpUsername);
        setHttpPassword(httpPassword);
        setPostScore(postScore);
        setCategory(category);
        setNoIssuesScore(noIssuesScore);
        setIssuesScore(issuesScore);
        setNoIssuesNotification(noIssuesNotification);
        setIssuesNotification(issuesNotification);

        // old values - not used anymore. will be deleted in further releases
        this.path = null;
        this.projectPath = null;
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


    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        List<ReportInfo> issueInfos = readSonarReports(listener, filePath);
        if (issueInfos == null) {
            throw new AbortException(getLocalized("jenkins.plugin.validation.path.no.project.config.available"));
        }

        Multimap<String, Issue> file2issues = generateFilenameToIssuesMapFilteredByPredicates(issueInfos);

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
        GerritAuthData.Basic authData = new GerritAuthData.Basic(
                gerritConfig.getGerritFrontEndUrl(),
                isOverrideCredentials() ? httpUsername : gerritConfig.getGerritHttpUserName(),
                isOverrideCredentials() ? httpPassword : gerritConfig.getGerritHttpPassword(),
                gerritConfig.isUseRestApi());
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
            final Map<String, FileInfo> files = revision.files();
            file2issues = Multimaps.filterKeys(file2issues, new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String input) {
                    return input != null && files.keySet().contains(input);
                }
            });

//            logResultMap(file2issues, "Filter issues by changed files: {0} elements");

            if (isChangedLinesOnly()) {
                // Step 4a - Filter issues by changed lines in file only
                filterIssuesByChangedLines(file2issues, revision);
//                logResultMap(file2issues, "Filter issues by changed lines: {0} elements");
            }

            // Step 6 - Send review to Gerrit
            ReviewInput reviewInput = getReviewResult(file2issues);

            // Step 7 - Post review
            revision.review(reviewInput);
            logMessage(listener, "jenkins.plugin.review.sent", Level.INFO);
        } catch (RestApiException e) {
            LOGGER.log(Level.SEVERE, "Unable to post review: " + e.getMessage(), e);
            throw new AbortException("Unable to post review: " + e.getMessage());
        }
    }

    @VisibleForTesting
    Multimap<String, Issue> generateFilenameToIssuesMapFilteredByPredicates(List<ReportInfo> issueInfos) {
        Multimap<String, Issue> file2issues = LinkedListMultimap.create();
        for (ReportInfo info : issueInfos) {

            Report report = info.report;

            // Step 1 - Filter issues by issues only predicates
            Iterable<Issue> filtered = filterIssuesByPredicates(report.getIssues());

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
        return new CustomReportFormatter(finalIssues.values(), someIssuesToPostText, noIssuesToPostText).getMessage();
    }

    private int getReviewMark(int finalIssuesCount) {
        String mark = finalIssuesCount > 0 ? issuesScore : noIssuesScore;
        return parseNumber(mark, DescriptorImpl.DEFAULT_SCORE);
    }

    public List<SubJobConfig> getSubJobConfigs() {
        return getSubJobConfigs(true);
    }

    public List<SubJobConfig> getSubJobConfigs(boolean addDefault) {
        if (subJobConfigs == null) {
            subJobConfigs = new ArrayList<SubJobConfig>();
            // add configuration from previous plugin version
          /*  if (path != null || projectPath != null) {
                subJobConfigs.add(new SubJobConfig(projectPath, path));
            } else */
            if (addDefault) {
                subJobConfigs.add(DescriptorImpl.JOB_CONFIG);
            }
        }
        return subJobConfigs;
    }

    private NotifyHandling getNotificationSettings(int finalIssuesCount) {
        return finalIssuesCount > 0 ?
                NotifyHandling.valueOf(issuesNotification) :
                NotifyHandling.valueOf(noIssuesNotification);
    }

    private int parseNumber(String number, int deflt) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return deflt;
        }

    }

    @VisibleForTesting
    ReviewInput getReviewResult(Multimap<String, Issue> finalIssues) {
        String reviewMessage = getReviewMessage(finalIssues);
        ReviewInput reviewInput = new ReviewInput().message(reviewMessage);

        int finalIssuesCount = finalIssues.size();

        reviewInput.notify = getNotificationSettings(finalIssuesCount);

        if (postScore) {
            reviewInput.label(category, getReviewMark(finalIssuesCount));
        }

        reviewInput.comments = new HashMap<String, List<ReviewInput.CommentInput>>();
        for (String file : finalIssues.keySet()) {
            reviewInput.comments.put(file, Lists.newArrayList(
                            Collections2.transform(finalIssues.get(file),
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
                                            commentInput.message = new CustomIssueFormatter(input, issueComment, getSonarURL()).getMessage();
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
    Iterable<Issue> filterIssuesByPredicates(List<Issue> issues) {
        Severity sev = Severity.valueOf(severity);
        return Iterables.filter(issues,
                Predicates.and(
                        ByMinSeverityPredicate.apply(sev),
                        ByNewPredicate.apply(isNewIssuesOnly()))
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

        public static final String PROJECT_PATH = "";
        public static final String SONAR_REPORT_PATH = "target/sonar/sonar-report.json";
        public static final String SONAR_URL = "http://localhost:9000";
        public static final String SEVERITY = Severity.MAJOR.name();
        public static final String NO_ISSUES_TEXT = Localization.getLocalized("jenkins.plugin.default.review.title.no.issues");
        public static final String SOME_ISSUES_TEXT = Localization.getLocalized("jenkins.plugin.default.review.title.issues");
        public static final String ISSUE_COMMENT_TEXT = Localization.getLocalized("jenkins.plugin.default.review.body");
        public static final String NOTIFICATION_RECIPIENT_NO_ISSUES_STR = NOTIFICATION_RECIPIENT_NO_ISSUES.toString();
        public static final String NOTIFICATION_RECIPIENT_SOME_ISSUES_STR = NOTIFICATION_RECIPIENT_SOME_ISSUES.toString();
        public static final String CATEGORY = "Code-Review";
        public static final String NO_ISSUES_SCORE = "1";
        public static final String SOME_ISSUES_SCORE = "-1";
        public static final boolean POST_SCORE = true;
        public static final boolean OVERRIDE_CREDENTIALS = false;
        public static final boolean NEW_ISSUES_ONLY = false;
        public static final boolean CHANGED_LINES_ONLY = false;

        public static final SubJobConfig JOB_CONFIG = new SubJobConfig(PROJECT_PATH, SONAR_REPORT_PATH);
        public static final List<SubJobConfig> JOB_CONFIGS = Arrays.<SubJobConfig>asList(JOB_CONFIG);
        public static final int DEFAULT_SCORE = 0;

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
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckSonarURL(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.trim().length() == 0) {
                return FormValidation.warning(getLocalized("jenkins.plugin.validation.sonar.url.empty"));
            }
            try {
                new URL(value);
            } catch (MalformedURLException e) {
                return FormValidation.warning(getLocalized("jenkins.plugin.validation.sonar.url.invalid"));
            }
            return FormValidation.ok();
        }

        public FormValidation doTestConnection(@QueryParameter("httpUsername") final String httpUsername,
                                               @QueryParameter("httpPassword") final String httpPassword,
                                               @QueryParameter("gerritServerName") final String gerritServerName) throws IOException, ServletException {
            if (httpUsername == null) {
                return FormValidation.error("jenkins.plugin.error.gerrit.user.empty");
            }
            if (gerritServerName == null) {
                return FormValidation.error("jenkins.plugin.error.gerrit.server.empty");
            }
            IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(gerritServerName);
            if (gerritConfig == null) {
                return FormValidation.error("jenkins.plugin.error.gerrit.config.empty");
            }

            if (!gerritConfig.isUseRestApi()) {
                return FormValidation.error("jenkins.plugin.error.gerrit.restapi.off");
            }

            GerritServer server = PluginImpl.getServer_(gerritServerName);
            if (server == null) {
                return FormValidation.error("jenkins.plugin.error.gerrit.server.null");
            }
            return server.getDescriptor().doTestRestConnection(gerritConfig.getGerritFrontEndUrl(), httpUsername, httpPassword/*, gerritConfig.isUseRestApi()*/);

        }

        public List<String> getGerritServerNames() {
            return PluginImpl.getServerNames_();
        }

        /**
         * Performs on-the-fly validation of the form field 'noIssuesToPostText'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckNoIssuesToPostText(@QueryParameter String value) {
            if (value.trim().length() == 0) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.title.empty"));
            }
            return FormValidation.ok();
        }

        /**
         * Performs on-the-fly validation of the form field 'someIssuesToPostText'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckSomeIssuesToPostText(@QueryParameter String value) {
            if (value.trim().length() == 0) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.title.empty"));
            }
            return FormValidation.ok();
        }

        /**
         * Performs on-the-fly validation of the form field 'issueComment'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckIssueComment(@QueryParameter String value) {
            if (value.trim().length() == 0) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.body.empty"));
            }
            return FormValidation.ok();
        }

        //todo validate subconfigs?

        /**
         * Performs on-the-fly validation of the form field 'severity'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckSeverity(@QueryParameter String value) {
            if (value == null || Severity.valueOf(value) == null) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.severity.unknown"));
            }
            return FormValidation.ok();
        }

        /**
         * Performs on-the-fly validation of the form field 'noIssuesScore'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckNoIssuesScore(@QueryParameter String value) {
            return checkScore(value);
        }

        /**
         * Performs on-the-fly validation of the form field 'issuesScore'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckIssuesScore(@QueryParameter String value) {
            return checkScore(value);
        }

        private FormValidation checkScore(@QueryParameter String value) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.score.not.numeric"));
            }
            return FormValidation.ok();
        }

        /**
         * Performs on-the-fly validation of the form field 'noIssuesNotification'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckNoIssuesNotification(@QueryParameter String value) {
            return checkNotificationType(value);
        }

        /**
         * Performs on-the-fly validation of the form field 'issuesNotification'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        @SuppressWarnings(value = "unused")
        public FormValidation doCheckIssuesNotification(@QueryParameter String value) {
            return checkNotificationType(value);
        }

        private FormValidation checkNotificationType(@QueryParameter String value) {
            if (value == null || NotifyHandling.valueOf(value) == null) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.notification.recipient.unknown"));
            }
            return FormValidation.ok();
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

    public String getSeverity() {
        return severity;
    }

    public boolean isChangedLinesOnly() {
        return changedLinesOnly;
    }

    public boolean isNewIssuesOnly() {
        return newIssuesOnly;
    }

    public String getSonarURL() {
        return sonarURL;
    }

    public String getNoIssuesToPostText() {
        return noIssuesToPostText;
    }

    public String getSomeIssuesToPostText() {
        return someIssuesToPostText;
    }

    public @Nonnull String getIssueComment() {
        return issueComment;
    }

    public boolean isOverrideCredentials() {
        return overrideCredentials;
    }

    public String getHttpUsername() {
        return httpUsername;
    }

    public String getHttpPassword() {
        return httpPassword;
    }

    @SuppressWarnings(value = "unused")
    public boolean isPostScore() {
        return postScore;
    }

    public String getCategory() {
        return category;
    }

    @SuppressWarnings(value = "unused")
    public String getNoIssuesScore() {
        return noIssuesScore;
    }

    @SuppressWarnings(value = "unused")
    public String getNoIssuesNotification() {
        return noIssuesNotification;
    }

    @SuppressWarnings(value = "unused")
    @Nonnull
    public String getIssuesNotification() {
        return issuesNotification;
    }

    @SuppressWarnings(value = "unused")
    public String getIssuesScore() {
        return issuesScore;
    }

    @DataBoundSetter
    public void setSonarURL(String sonarURL) {
        this.sonarURL = MoreObjects.firstNonNull(sonarURL, DescriptorImpl.SONAR_URL);
    }

    @DataBoundSetter
    public void setSubJobConfigs(List<SubJobConfig> subJobConfigs) {
        this.subJobConfigs = MoreObjects.firstNonNull(subJobConfigs, DescriptorImpl.JOB_CONFIGS);
    }

    @DataBoundSetter
    public void setSeverity(String severity) {
        this.severity = MoreObjects.firstNonNull(severity, DescriptorImpl.SEVERITY);
    }

    @DataBoundSetter
    public void setChangedLinesOnly(boolean changedLinesOnly) {
        this.changedLinesOnly = changedLinesOnly;
    }

    @DataBoundSetter
    public void setNewIssuesOnly(boolean newIssuesOnly) {
        this.newIssuesOnly = newIssuesOnly;
    }

    @DataBoundSetter
    public void setNoIssuesToPostText(String noIssuesToPostText) {
        this.noIssuesToPostText = MoreObjects.firstNonNull(noIssuesToPostText, DescriptorImpl.NO_ISSUES_TEXT);
    }

    @DataBoundSetter
    public void setSomeIssuesToPostText(String someIssuesToPostText) {
        this.someIssuesToPostText = MoreObjects.firstNonNull(someIssuesToPostText, DescriptorImpl.SOME_ISSUES_TEXT);
    }

    @DataBoundSetter
    public void setIssueComment(@Nonnull String issueComment) {
        this.issueComment = issueComment;//MoreObjects.firstNonNull(issueComment, DescriptorImpl.ISSUE_COMMENT_TEXT);
    }

    @DataBoundSetter
    public void setOverrideCredentials(boolean overrideCredentials) {
        this.overrideCredentials = overrideCredentials;
    }

    @DataBoundSetter
    public void setHttpUsername(String httpUsername) {
        this.httpUsername = httpUsername;
    }

    @DataBoundSetter
    public void setHttpPassword(String httpPassword) {
        this.httpPassword = httpPassword;
    }

    @DataBoundSetter
    public void setPostScore(boolean postScore) {
        this.postScore = postScore;
    }

    @DataBoundSetter
    public void setCategory(String category) {
        this.category = MoreObjects.firstNonNull(category, DescriptorImpl.CATEGORY);
    }

    @DataBoundSetter
    public void setNoIssuesScore(String noIssuesScore) {
        this.noIssuesScore = MoreObjects.firstNonNull(noIssuesScore, DescriptorImpl.NO_ISSUES_SCORE);
    }

    @DataBoundSetter
    public void setIssuesScore(String issuesScore) {
        this.issuesScore = MoreObjects.firstNonNull(issuesScore, DescriptorImpl.SOME_ISSUES_SCORE);
    }

    @DataBoundSetter
    public void setNoIssuesNotification(String noIssuesNotification) {
        this.noIssuesNotification = MoreObjects.firstNonNull(noIssuesNotification, DescriptorImpl.NOTIFICATION_RECIPIENT_NO_ISSUES_STR);
    }

    @DataBoundSetter
    public void setIssuesNotification(String issuesNotification) {
        this.issuesNotification = MoreObjects.firstNonNull(issuesNotification, DescriptorImpl.NOTIFICATION_RECIPIENT_SOME_ISSUES_STR);
    }
}

