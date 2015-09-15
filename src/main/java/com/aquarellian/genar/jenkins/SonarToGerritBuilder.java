package com.aquarellian.genar.jenkins;

import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.converter.BasicIssueFormatter;
import com.aquarellian.genar.data.entity.Component;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import com.aquarellian.genar.data.entity.Severity;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
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
import java.util.*;

/**
 * @author Tatiana Didik
 */
public class SonarToGerritBuilder extends Builder {

    private static final String DEFAULT_PATH = "target/sonar/sonar-report.json";
    public static final String GERRIT_FILE_DELIMITER = "/";

    private final String path;
    private final Severity severity;
    private final boolean changedLinesOnly;
    private final boolean newIssuesOnly;
    private final boolean extendedLogging = true;

    @DataBoundConstructor
    public SonarToGerritBuilder(String path, String severity, boolean changedLinesOnly, boolean isNewIssuesOnly) {
        this.path = MoreObjects.firstNonNull(path, DEFAULT_PATH);
        this.severity = MoreObjects.firstNonNull(Severity.valueOf(severity), Severity.MAJOR);
        this.changedLinesOnly = changedLinesOnly;
        this.newIssuesOnly = isNewIssuesOnly;
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

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException,
            InterruptedException {
        FilePath reportPath = build.getWorkspace().child(getPath());
        logMessage(listener, "[GENAR] Getting Sonar Report from: " + reportPath);

        SonarReportBuilder builder = new SonarReportBuilder();
        String reportJson = reportPath.readToString();
        Report report = builder.fromJson(reportJson);
        logMessage(listener, String.format("[GENAR] Report has loaded and contains %d issues ", report.getIssues().size()));

        // Step 1 - Filter issues by issues only predicates
        Iterable<Issue> filtered = filterIssuesByPredicates(report);
        logMessage(listener, String.format("[GENAR] %d issues has left after filtration by predicates (severity, ... etc)", Lists.newArrayList(filtered).size()));

        // Step 2 - Calculate real file name for issues and store to multimap
        Multimap<String, Issue> file2issues = generateFilenameToIssuesMap(report, filtered);
        logResultMap(listener, file2issues, String.format("\n[GENAR] file2issues map contains %d elements", file2issues.keySet().size()));

        // Step 3 - Prepare Gerrit REST API client
        String gerritServerName = GerritTrigger.getTrigger(build.getProject()).getServerName();
        IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(gerritServerName);

        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(gerritConfig.getGerritFrontEndUrl(),
                gerritConfig.getGerritHttpUserName(), gerritConfig.getGerritHttpPassword());
        GerritApi gerritApi = gerritRestApiFactory.create(authData);
        try {
            EnvVars envVars = build.getEnvironment(listener);
            int changeNumber = Integer.valueOf(envVars.get("GERRIT_CHANGE_NUMBER"));
            int patchSetNumber = Integer.valueOf(envVars.get("GERRIT_PATCHSET_NUMBER"));
            RevisionApi revision = gerritApi.changes().id(changeNumber).revision(patchSetNumber);
            logMessage(listener, String.format("[GENAR] Connected to Gerrit: server name: %s. Change Number: %d, PatchSetNumber: %d", gerritServerName, changeNumber, patchSetNumber));

            // Step 4 - Filter issues by changed files
            final Map<String, FileInfo> files = revision.files();
            file2issues = Multimaps.filterKeys(file2issues, new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String input) {
                    return input != null && files.keySet().contains(input);
                }
            });

            logResultMap(listener, file2issues, "\n[GENAR] Filter issues by changed files:");

            if (isChangedLinesOnly()) {
                // Step 4a - Filter issues by changed lines in file only
                filterIssuesByChangedLines(file2issues, revision);
                logResultMap(listener, file2issues, "\n[GENAR] Filter issues by changed lines:");
            }

            // Step 6 - Send review to Gerrit
            ReviewInput reviewInput = getReviewResult(file2issues);

            // Step 7 - Post review
            revision.review(reviewInput);
            logMessage(listener, "[GENAR] Review has been sent");
        } catch (RestApiException e) {
            listener.error(e.getMessage());
        }

        return true;
    }

    private void logMessage(BuildListener listener, String message) {
        if (extendedLogging) {
            listener.getLogger().println(message);
        }
    }

    private void logResultMap(BuildListener listener, Multimap<String, Issue> file2issues, String message) {
        if (extendedLogging) {
            listener.getLogger().println(message);
            if (file2issues.isEmpty()) {
                listener.getLogger().println("None");
            }
            for (String file : file2issues.keySet()) {
                Collection<Issue> issues = file2issues.get(file);
                listener.getLogger().println(String.format("\n [GENAR] File %s contains %d issues: %s", file, issues.size(), issues));
            }
        }
    }

    private String getReviewMessage(Multimap<String, Issue> finalIssues) {
        return finalIssues.size() > 0 ? "Sonar violations have been found." : "Sonar violations have not been found.";
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
                                            commentInput.message = new BasicIssueFormatter(input).getMessage();
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
                        BySeverityPredicate.equalOrHigher(getSeverity()),
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
                return FormValidation.warning("Please set a path");
            File f = new File(value);
            if (!f.exists())
                return FormValidation.error("No such file:" + value);
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Post Sonar issues as Gerrit comments";
        }
    }

    private static class BySeverityPredicate implements Predicate<Issue> {

        private final Severity severity;

        public static BySeverityPredicate equalOrHigher(Severity severity) {
            return new BySeverityPredicate(severity);
        }

        private BySeverityPredicate(Severity severity) {
            this.severity = severity;
        }

        @Override
        public boolean apply(Issue issue) {
            return issue.getSeverity().equals(severity) || issue.getSeverity().ordinal() >= severity.ordinal();
        }
    }

    private static class ByNewPredicate implements Predicate<Issue> {

        private final boolean anew;

        public static ByNewPredicate apply(boolean anew) {
            return new ByNewPredicate(anew);
        }

        private ByNewPredicate(boolean anew) {
            this.anew = anew;
        }

        @Override
        public boolean apply(Issue issue) {
            return !anew || issue.isNew();
        }
    }

}

