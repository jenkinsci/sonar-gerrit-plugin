package com.aquarellian.plugins.jenkins.sonargerrit;

import com.aquarellian.plugins.jenkins.sonargerrit.data.SonarReportBuilder;
import com.aquarellian.plugins.jenkins.sonargerrit.data.converter.CustomIssueFormatter;
import com.aquarellian.plugins.jenkins.sonargerrit.data.converter.CustomReportFormatter;
import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Component;
import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Issue;
import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Report;
import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Severity;
import com.aquarellian.plugins.jenkins.sonargerrit.data.predicates.ByMinSeverityPredicate;
import com.aquarellian.plugins.jenkins.sonargerrit.data.predicates.ByNewPredicate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.gerrit.extensions.api.GerritApi;
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
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.aquarellian.plugins.jenkins.sonargerrit.util.Localization.getLocalized;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */

public class SonarToGerritBuilder extends Builder {

    private static final String DEFAULT_PATH = "target/sonar/sonar-report.json";
    private static final String DEFAULT_SONAR_URL = "http://localhost:9000";
    public static final String GERRIT_FILE_DELIMITER = "/";

    private static final Logger LOGGER = Logger.getLogger(SonarToGerritBuilder.class.getName());
    public static final String GERRIT_CHANGE_NUMBER_ENV_VAR_NAME = "GERRIT_CHANGE_NUMBER";
    public static final String GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME = "GERRIT_PATCHSET_NUMBER";

    private final String sonarURL;
    private final String path;
    private final Severity severity;
    private final boolean changedLinesOnly;
    private final boolean newIssuesOnly;
    private final String noIssuesToPostText;
    private final String someIssuesToPostText;
    private final String issueComment;


    @DataBoundConstructor
    public SonarToGerritBuilder(String sonarURL, String path,
                                String severity, boolean changedLinesOnly, boolean isNewIssuesOnly,
                                String noIssuesToPostText, String someIssuesToPostText, String issueComment) {
        this.sonarURL = MoreObjects.firstNonNull(sonarURL, DEFAULT_SONAR_URL);
        this.path = MoreObjects.firstNonNull(path, DEFAULT_PATH);
        this.severity = MoreObjects.firstNonNull(Severity.valueOf(severity), Severity.MAJOR);
        this.changedLinesOnly = changedLinesOnly;
        this.newIssuesOnly = isNewIssuesOnly;
        this.noIssuesToPostText = noIssuesToPostText;
        this.someIssuesToPostText = someIssuesToPostText;
        this.issueComment = issueComment;
    }

    public String getPath() {
        return path;
    }

    public Severity getSeverity() {
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

    public String getIssueComment() {
        return issueComment;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException,
            InterruptedException {
        FilePath reportPath = build.getWorkspace().child(getPath());
        LOGGER.log(Level.INFO, "Getting Sonar Report from: {0}", reportPath);

        SonarReportBuilder builder = new SonarReportBuilder();
        String reportJson = reportPath.readToString();
        Report report = builder.fromJson(reportJson);
        LOGGER.log(Level.INFO, "Report has loaded and contains {0} issues", report.getIssues().size());

        // Step 1 - Filter issues by issues only predicates
        Iterable<Issue> filtered = filterIssuesByPredicates(report);
        LOGGER.log(Level.INFO, "{0} issues left after filtration by predicates (severity, ... etc)", Lists.newArrayList(filtered).size());

        // Step 2 - Calculate real file name for issues and store to multimap
        Multimap<String, Issue> file2issues = generateFilenameToIssuesMap(report, filtered);
        logResultMap(file2issues, "Map file2issues contains {0} elements");

        // Step 3 - Prepare Gerrit REST API client
        String gerritServerName = GerritTrigger.getTrigger(build.getProject()).getServerName();
        IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(gerritServerName);

        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(gerritConfig.getGerritFrontEndUrl(),
                gerritConfig.getGerritHttpUserName(), gerritConfig.getGerritHttpPassword());
        GerritApi gerritApi = gerritRestApiFactory.create(authData);
        try {
            EnvVars envVars = build.getEnvironment(listener);
            int changeNumber = Integer.parseInt(envVars.get(GERRIT_CHANGE_NUMBER_ENV_VAR_NAME));
            int patchSetNumber = Integer.parseInt(envVars.get(GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME));
            RevisionApi revision = gerritApi.changes().id(changeNumber).revision(patchSetNumber);
            LOGGER.log(Level.INFO, "Connected to Gerrit: server name: {0}. Change Number: {1}, PatchSetNumber: {2}", new Object[]{gerritServerName, changeNumber, patchSetNumber});

            // Step 4 - Filter issues by changed files
            final Map<String, FileInfo> files = revision.files();
            file2issues = Multimaps.filterKeys(file2issues, new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String input) {
                    return input != null && files.keySet().contains(input);
                }
            });

            logResultMap(file2issues, "Filter issues by changed files: {0} elements");

            if (isChangedLinesOnly()) {
                // Step 4a - Filter issues by changed lines in file only
                filterIssuesByChangedLines(file2issues, revision);
                logResultMap(file2issues, "Filter issues by changed lines: {0} elements");
            }

            // Step 6 - Send review to Gerrit
            ReviewInput reviewInput = getReviewResult(file2issues);

            // Step 7 - Post review
            revision.review(reviewInput);
            LOGGER.log(Level.INFO, "Review has been sent");
        } catch (RestApiException e) {
            LOGGER.severe(e.getMessage());
        }

        return true;
    }

    private void logResultMap(Multimap<String, Issue> file2issues, String message) {
        LOGGER.log(Level.INFO, message, file2issues.keySet().size());
        for (String file : file2issues.keySet()) {
            Collection<Issue> issues = file2issues.get(file);
            String issuesAsString = Joiner.on(System.lineSeparator()).join(issues);
            LOGGER.log(Level.INFO, "File {0} contains {1} issues:{2}{3}", new Object[]{file, issues.size(), System.lineSeparator(), issuesAsString});
        }
    }

    private String getReviewMessage(Multimap<String, Issue> finalIssues) {
        return new CustomReportFormatter(finalIssues.values(), someIssuesToPostText, noIssuesToPostText).getMessage();
    }

    @VisibleForTesting
    ReviewInput getReviewResult(Multimap<String, Issue> finalIssues) {
        String reviewMessage = getReviewMessage(finalIssues);
        ReviewInput reviewInput = new ReviewInput().message(reviewMessage);

        reviewInput.comments = new HashMap<String, List<ReviewInput.CommentInput>>();
        for (String file : finalIssues.keySet()) {
            reviewInput.comments.put(file, Lists.newArrayList(
                            Collections2.transform(finalIssues.get(file),
                                    new Function<Issue, ReviewInput.CommentInput>() {
                                        @Nullable
                                        @Override
                                        public ReviewInput.CommentInput apply(@Nullable Issue input) {
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
    Multimap<String, Issue> generateFilenameToIssuesMap(Report report, Iterable<Issue> filtered) {
        Multimap<String, Issue> file2issues = LinkedListMultimap.create();

/*       The next code prepares data to process situations like this one:
        {
            "key": "com.maxifier.guice:guice-bootstrap",
            "path": "guice-bootstrap"
        },
        {
            "key": "com.maxifier.guice:guice-bootstrap:src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java",
            "path": "src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java",
            "moduleKey": "com.maxifier.guice:guice-bootstrap",
            "status": "SAME"
        }
        */
        Map<String, String> component2module = Maps.newHashMap();
        Map<String, String> component2path = Maps.newHashMap();

        for (Component component : report.getComponents()) {
            component2path.put(component.getKey(), component.getPath());
        }
        for (Component component : report.getComponents()) {
            if (component.getModuleKey() != null) {
                component2module.put(component.getKey(), component2path.get(component.getModuleKey()));
            }
        }


        // generating map consisting of real file names to corresponding issues collections.
        for (Issue issue : filtered) {
            String issueComponent = issue.getComponent();
            String moduleName = component2module.get(issueComponent);
            String componentPath = component2path.get(issueComponent);
            String realFileName = moduleName != null ? moduleName + GERRIT_FILE_DELIMITER + componentPath : componentPath;
            file2issues.put(realFileName, issue);

        }
        return file2issues;
    }

    @VisibleForTesting
    Iterable<Issue> filterIssuesByPredicates(Report report) {
        List<Issue> issues = report.getIssues();
        return Iterables.filter(issues,
                Predicates.and(
                        ByMinSeverityPredicate.apply(getSeverity()),
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

    /**
     * Descriptor for {@link SonarToGerritBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/SonarToGerritBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'path'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p/>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckPath(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.warning(getLocalized("jenkins.plugin.validation.path.empty"));
            File f = new File(value);
            if (!f.exists())
                return FormValidation.error(String.format(getLocalized("jenkins.plugin.validation.path.no.such.file"), value));
            return FormValidation.ok();
        }

        public FormValidation doCheckSonarURL(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.warning(getLocalized("jenkins.plugin.validation.sonar.url.empty"));
            }
            try {
                new URL(value);
            } catch (MalformedURLException e) {
                return FormValidation.warning(getLocalized("jenkins.plugin.validation.sonar.url.invalid"));
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckNoIssuesToPostText(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.title.empty"));
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckSomeIssuesToPostText(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.title.empty"));
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckIssueComment(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error(getLocalized("jenkins.plugin.validation.review.body.empty"));
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            //todo check if gerrit trigger installed
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

}

