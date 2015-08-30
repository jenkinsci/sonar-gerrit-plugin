package com.aquarellian.genar.jenkins;

import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import com.aquarellian.genar.data.entity.Severity;
import com.google.common.base.*;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
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
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 29.08.2015 16:54
 * <p/>
 * $Id$
 */
public class SonarToGerritBuilder extends Builder {

    private static final String DEFAULT_PATH = "target/sonar/sonar-report.json";

    private final String path;
    private final Severity severity;
    private final boolean changedLinesOnly;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public SonarToGerritBuilder(String path, String severity, boolean changedLinesOnly) {
        this.path = MoreObjects.firstNonNull(path, DEFAULT_PATH);
        this.severity = MoreObjects.firstNonNull(Severity.valueOf(severity), Severity.MAJOR);
        this.changedLinesOnly = changedLinesOnly;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
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

        EnvVars envVars = build.getEnvironment(listener);
        int changeNumber = Integer.valueOf(envVars.get("GERRIT_CHANGE_NUMBER"));
        int patchSetNumber = Integer.valueOf(envVars.get("GERRIT_PATCHSET_NUMBER"));

        String gerritServerName = GerritTrigger.getTrigger(build.getProject()).getServerName();
        IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(gerritServerName);

        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(gerritConfig.getGerritFrontEndUrl(),
                gerritConfig.getGerritHttpUserName(), gerritConfig.getGerritHttpPassword());
        GerritApi gerritApi = gerritRestApiFactory.create(authData);
        try {
            RevisionApi revision = gerritApi.changes().id(changeNumber).revision(patchSetNumber);
            Map<String, FileInfo> files = revision.files();

            List<Issue> issues = report.getIssues();
//            listener.getLogger().println("Count of issues in report = " + issues.size());
//            listener.getLogger().println(report);
            Iterable<Issue> filtered = Iterables.filter(issues,
                    Predicates.and(
                            BySeverityPredicate.equalOrHigher(severity),
                            ByLinePredicate.equalOrDoesNotMatter(changedLinesOnly, revision)
                    )
            );

            HashMultimap<String, Issue> file2issues = HashMultimap.create();
            for (Issue issue : filtered) {
                final String component = issue.getComponent();
                Optional<String> owner = Iterables.tryFind(files.keySet(), new Predicate<String>() {
                    @Override
                    public boolean apply(@Nullable String s) {
                        return s.equals(component);   //todo
                    }
                });
                if (owner.isPresent()) {
                    file2issues.put(owner.get(), issue);
                }
            }

            ReviewInput reviewInput = new ReviewInput().message("TODO Message From Jenkins");

            reviewInput.comments = new HashMap<String, List<ReviewInput.CommentInput>>();
            for (Map.Entry<String, Issue> fileIssue : file2issues.entries()) {
                ReviewInput.CommentInput commentInput = new ReviewInput.CommentInput();
                commentInput.line = fileIssue.getValue().getLine();
                commentInput.message = fileIssue.getValue().getMessage();

                reviewInput.comments.put(fileIssue.getKey(), ImmutableList.of(commentInput));
            }

            revision.review(reviewInput);
        } catch (RestApiException e) {
            listener.error(e.getMessage());
        }

        return true;
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
                return Collections.EMPTY_SET;
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

