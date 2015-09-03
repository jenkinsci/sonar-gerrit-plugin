package com.aquarellian.genar.jenkins;

import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.entity.Component;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import com.aquarellian.genar.data.entity.Severity;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
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

    @DataBoundConstructor
    public SonarToGerritBuilder(String path, String severity, boolean changedLinesOnly) {
        this.path = MoreObjects.firstNonNull(path, DEFAULT_PATH);
        this.severity = MoreObjects.firstNonNull(Severity.valueOf(severity), Severity.MAJOR);
        this.changedLinesOnly = changedLinesOnly;
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

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException,
            InterruptedException {
        FilePath reportPath = build.getWorkspace().child(getPath());
        listener.getLogger().println("Getting Sonar Report from: " + reportPath);
        SonarReportBuilder builder = new SonarReportBuilder();
        String reportJson = reportPath.readToString();
        Report report = builder.fromJson(reportJson);

        // Step 1 - Filter issues by issues only predicates
        Iterable<Issue> filtered = filterIssuesByPredicates(report);

        // Step 2 - Calculate real file name for issues and store to multimap
        Multimap<String, Issue> file2issues = generateFilenameToIssuesMap(report, filtered);

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

            // Step 4 - Filter issues by changed files
            final Map<String, FileInfo> files = revision.files();
            file2issues = Multimaps.filterKeys(file2issues, new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String input) {
                    return input != null && files.keySet().contains(input);
                }
            });

            Map<String, Collection<Issue>> finalIssues = file2issues.asMap();

            if (isChangedLinesOnly()) {
                // Step 4a - Filter issues by changed lines in file only
                finalIssues = filterIssuesByChangedLines(finalIssues, revision);
            }

            // Step 6 - Send review to Gerrit
            ReviewInput reviewInput = getReviewResult(finalIssues);

            // Step 7 - Post review
            revision.review(reviewInput);
        } catch (RestApiException e) {
            listener.error(e.getMessage());
        }

        return true;
    }

    @VisibleForTesting
    ReviewInput getReviewResult(Map<String, Collection<Issue>> finalIssues) {
        ReviewInput reviewInput = new ReviewInput().message("TODO Message From Sonar");

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
                                            commentInput.message = input.getMessage();
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
    Map<String, Collection<Issue>> filterIssuesByChangedLines(Map<String, Collection<Issue>> finalIssues, RevisionApi revision) throws RestApiException {
        Map<String, Collection<Issue>> res = new HashMap<String, Collection<Issue>>();

        for (String filename : finalIssues.keySet()) {
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
                    for (int i = start; i <= end; i++) {    // todo use guava for this purpose
                        rangeSet.add(i);
                    }
//                    rangeSet.add(Range.closed(start, end));
                    processed += contentEntry.b.size();
                }
            }

            if (res.get(filename) == null) {
                res.put(filename, new ArrayList<Issue>());
            }
            for (Issue i : finalIssues.get(filename)) {
                if (rangeSet.contains(i.getLine())) {
                    res.get(filename).add(i);
                }
            }
        }
        return res;
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
        return Iterables.filter(issues, BySeverityPredicate.equalOrHigher(getSeverity()));
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

    private static class ByLinePredicate implements Predicate<Issue> {

        private final boolean changedLinesOnly;
        private final RevisionApi revision;

        public static ByLinePredicate equalOrDoesNotMatter(boolean changedLinesOnly, RevisionApi revision) {
            return new ByLinePredicate(changedLinesOnly, revision);
        }

        private ByLinePredicate(boolean changedLinesOnly, RevisionApi revision) {
            this.changedLinesOnly = changedLinesOnly;
            this.revision = revision;
        }

        @Override
        public boolean apply(Issue issue) {
            return !changedLinesOnly || getChangedLines(issue).contains(issue.getLine());
        }

        private Set<Integer> getChangedLines(Issue issue) {
            final List<DiffInfo.ContentEntry> content;
            try {
                content = revision.file(issue.getComponent()).diff().content;  //todo replace issue.getComponent() by actual filename
            } catch (RestApiException e) {
                return Collections.emptySet();
            }

            Iterable<DiffInfo.ContentEntry> filtered = Iterables.filter(content,
                    new Predicate<DiffInfo.ContentEntry>() {
                        @Override
                        public boolean apply(@Nullable DiffInfo.ContentEntry contentEntry) {
                            return contentEntry != null && !contentEntry.a.equals(contentEntry.b);
                        }
                    }
            );
            Iterable<Integer> changedLines = Iterables.transform(filtered, new Function<DiffInfo.ContentEntry, Integer>() {
                @Nullable
                @Override
                public Integer apply(@Nullable DiffInfo.ContentEntry contentEntry) {
                    return content.indexOf(contentEntry) + 1; // think about it. is that a right way to get changed line number?
                }
            });
            return Sets.newHashSet(changedLines);
        }
    }
}

