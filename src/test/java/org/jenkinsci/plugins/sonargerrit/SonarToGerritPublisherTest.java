package org.jenkinsci.plugins.sonargerrit;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gerrit.extensions.api.changes.*;
import com.google.gerrit.extensions.client.SubmitType;
import com.google.gerrit.extensions.common.ActionInfo;
import com.google.gerrit.extensions.common.CommentInfo;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.common.MergeableInfo;
import com.google.gerrit.extensions.common.RobotCommentInfo;
import com.google.gerrit.extensions.common.TestSubmitRuleInput;
import com.google.gerrit.extensions.restapi.BinaryResult;
import com.google.gerrit.extensions.restapi.RestApiException;
import hudson.FilePath;
import junit.framework.Assert;
import org.jenkinsci.plugins.sonargerrit.data.SonarReportBuilder;
import org.jenkinsci.plugins.sonargerrit.data.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.data.entity.Report;
import org.jenkinsci.plugins.sonargerrit.data.entity.Severity;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 30.08.2015 13:41
 *
 */
public class SonarToGerritPublisherTest {

    @Test
    public void testFilterByPredicates() throws IOException, InterruptedException, URISyntaxException {
        Report report = readreport();
        Assert.assertEquals(19, report.getIssues().size());

        // severity predicate
        Iterable<Issue> issues = new SonarToGerritPublisher("", null, Severity.CRITICAL.name(),Severity.CRITICAL.name(), true, false, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(2, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.MAJOR.name(), Severity.MAJOR.name(), true, false, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(12, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.INFO.name(), Severity.INFO.name(), true, false, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(19, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.MINOR.name(), Severity.MINOR.name(), true, false, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(18, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.BLOCKER.name(), Severity.BLOCKER.name(), true, false, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        // new issues only predicate
        issues = new SonarToGerritPublisher("", null, Severity.CRITICAL.name(), Severity.CRITICAL.name(), true, true, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(0, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.MAJOR.name(), Severity.MAJOR.name(), true, true, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.INFO.name(), Severity.INFO.name(), true, true, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.MINOR.name(), Severity.MINOR.name(), true, true, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        issues = new SonarToGerritPublisher("", null, Severity.BLOCKER.name(), Severity.BLOCKER.name(), true, true, "", "", "", false, "", "", true, "", "0", "0", "", "").filterIssuesToFailByPredicates(report.getIssues());
        Assert.assertEquals(0, Sets.newHashSet(issues).size());
    }

    @Test
    public void testGenerateRealNameMap() throws InterruptedException, IOException, URISyntaxException {
        Report report = readreport();
        Assert.assertEquals(19, report.getIssues().size());
        Multimap<String, Issue> multimap = SonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates("", report, report.getIssues());

        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(8, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("guice-jpa/src/main/java/com/magenta/guice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());

        multimap = SonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates("", report, report.getIssues());

        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(8, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("guice-jpa/src/main/java/com/magenta/guice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());

        SubJobConfig config = new SubJobConfig("testfolder", "");
        multimap = SonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates(config.getProjectPath(), report, report.getIssues());
        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(8, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("testfolder/guice-jpa/src/main/java/com/magenta/guice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("testfolder/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("testfolder/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-events/src/main/java/com/magenta/guice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-events/src/main/java/com/magenta/guice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-events/src/main/java/com/magenta/guice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());

        config = new SubJobConfig("testfolder/", "");
        multimap = SonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates(config.getProjectPath(), report, report.getIssues());
        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(8, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("testfolder/guice-jpa/src/main/java/com/magenta/guice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("testfolder/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("testfolder/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-events/src/main/java/com/magenta/guice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-events/src/main/java/com/magenta/guice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/guice-events/src/main/java/com/magenta/guice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());

        SubJobConfig config1 = new SubJobConfig("testfolder1/", "report1.json");
        SubJobConfig config2 = new SubJobConfig("testfolder2/", "report2.json");
        SonarToGerritPublisher sonarToGerritPublisher = new SonarToGerritPublisher("", Arrays.asList(config1, config2), Severity.MAJOR.name(), Severity.INFO.name(), true, false, "", "", "",  false, "", "",true, "", "0", "0", "", "");
        String resourcePath = getClass().getClassLoader().getResource("filter.json").getPath();
        FilePath resourceFolder = new FilePath(new File(resourcePath).getParentFile());
        List<SonarToGerritPublisher.ReportInfo> reportInfos = sonarToGerritPublisher.readSonarReports(null, resourceFolder);//todo assert
        multimap = sonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates(reportInfos);
        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(9, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("testfolder1/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("testfolder1/guice-jpa/src/main/java/com/magenta/guice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(5, multimap.get("testfolder1/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(3, multimap.get("testfolder2/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("testfolder2/guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/guice-events/src/main/java/com/magenta/guice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/guice-events/src/main/java/com/magenta/guice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/guice-events/src/main/java/com/magenta/guice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());

        report = readreport("report3_with-nested-subprojects.json");
        config = new SubJobConfig("testfolder/", "");
        multimap = SonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates(config.getProjectPath(), report, report.getIssues());
        Assert.assertEquals(8, multimap.size());
        Assert.assertEquals(3, multimap.get("testfolder/base/core/proj1/src/main/java/proj1/Proj1.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/core/proj2/sub2/src/main/java/com/proj2/sub2/SubProj2.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/core/proj2/sub2/sub22/sub2222/sub22222/src/main/java/com/proj2/sub2/SubProj22222.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/core/proj2/src/main/java/com/proj2/Proj2.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/com.acme.util/src/main/java/com/acme/util/Util.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/com.acme.app/src/main/java/com/acme/app/App.java").size());
    }

    @Test
    public void testFilterIssuesByChangedLines() throws InterruptedException, IOException, URISyntaxException, RestApiException {
        Report report = readreport();
        Assert.assertEquals(19, report.getIssues().size());

        Multimap<String, Issue> multimap = SonarToGerritPublisher.generateFilenameToIssuesMapFilteredByPredicates("", report, report.getIssues());

        // Map will describe which strings in each file should be marked as modified.
        // integer values are count of strings that affected by ContentEntry.
        // lines.indexOf(v) % 2 == 0 -> v is count of unchanged lines, !=0 -> count of changed lines
        Map<String, List<Integer>> path2changedValues = new HashMap<String, List<Integer>>();
        path2changedValues.put(
                // 35, 10, 40, 5, 4, 20, 100  means that in PluginsManager.java first 35 strings are unchanged, 36-45 are changed,
                // 46-86 - unchanged, 87-91 - changed, 92-95 are not changed, 96-115 changed  and 116-216 are not changed
                "guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java", Arrays.asList(35, 10, 40, 5, 4, 20, 100)
        );

        RevisionApi revApi = new DummyRevisionApi(path2changedValues);


        SonarToGerritPublisher builder = new SonarToGerritPublisher("", null, Severity.MAJOR.name(), Severity.INFO.name(), true, false, "", "", "", false, "", "", true, "", "0", "0", "", "");
        builder.filterIssuesByChangedLines(multimap, revApi);

        // list of lines commented by sonar : 37, 54,81, 99, 106, 108, 122, 162
        // list of lines affected by change : 37, 99, 106, 108
        Set<Integer> resultIssues = Sets.newHashSet(37, 99, 106, 108);

        Collection<Issue> issues = multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java");
        for (Issue issue : issues) {
            Assert.assertTrue(resultIssues.contains(issue.getLine()));
        }

        issues = multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java");
        Assert.assertEquals(0, issues.size());
    }

    @Test
    public void getReviewResultTest() throws InterruptedException, IOException, URISyntaxException, RestApiException {
        Multimap<String, Issue> finalIssues = LinkedListMultimap.create();
        finalIssues.put("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java", new DummyIssue());
        finalIssues.put("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java", new DummyIssue());
        SonarToGerritPublisher builder = new SonarToGerritPublisher("", null,  Severity.MAJOR.name(), Severity.INFO.name(), true, false,
                "No Issues Header", "Some Issues Header", "Issue Comment", false, "", "", true, "Test", "+1", "-1", "NONE", "OWNER");
        ReviewInput reviewResult = builder.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(-1, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.OWNER, reviewResult.notify);

        builder = new SonarToGerritPublisher("", null, Severity.BLOCKER.name(), Severity.INFO.name(), true, false,
                "No Issues Header", "Some Issues Header", "Issue Comment", false, "", "", false, "Test", "1", "-1", null, null);
        reviewResult = builder.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(null, reviewResult.labels);
        Assert.assertEquals(NotifyHandling.OWNER, reviewResult.notify);

        builder = new SonarToGerritPublisher("", null, Severity.MAJOR.name(), Severity.INFO.name(), true, false,
                "No Issues Header", "Some Issues Header", "Issue Comment", false, "", "", true, "Test", "0", "0", null, null);
        reviewResult = builder.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(0, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.OWNER, reviewResult.notify);

        builder = new SonarToGerritPublisher("", null, Severity.MAJOR.name(), Severity.INFO.name(), true, false,
                "No Issues Header", "Some Issues Header", "Issue Comment", false, "", "", true, "Test", "1test", "-1test", "NONE", "ALL");
        reviewResult = builder.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(0, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.ALL, reviewResult.notify);

        builder = new SonarToGerritPublisher("", null, Severity.MAJOR.name(), Severity.INFO.name(), true, false,
                "No Issues Header", "Some Issues Header", "Issue Comment", false, "", "", true, "Test", "1", "-1", null, null);
        finalIssues = LinkedListMultimap.create();
        reviewResult = builder.getReviewResult(finalIssues);
        Assert.assertEquals("No Issues Header", reviewResult.message);
        Assert.assertEquals(0, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(+1, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.NONE, reviewResult.notify);

        builder = new SonarToGerritPublisher("", null, Severity.MAJOR.name(), Severity.INFO.name(), true, false,
                "No Issues Header", "Some Issues Header", "Issue Comment", false, "", "", true, "Test", "1", "-1", "OWNER_REVIEWERS", "ALL");
        finalIssues = LinkedListMultimap.create();
        reviewResult = builder.getReviewResult(finalIssues);
        Assert.assertEquals("No Issues Header", reviewResult.message);
        Assert.assertEquals(0, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(NotifyHandling.OWNER_REVIEWERS, reviewResult.notify);
        Assert.assertEquals(+1, reviewResult.labels.get("Test").intValue());

    }

    private Report readreport() throws IOException, InterruptedException, URISyntaxException {
        return readreport("filter.json");
    }

    private Report readreport(String file) throws IOException, InterruptedException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource(file);

        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        return new SonarReportBuilder().fromJson(json);
    }

    private class DummyIssue extends Issue {
        @Override
        public Severity getSeverity() {
            return Severity.CRITICAL;
        }
    }

    private class DummyRevisionApi implements RevisionApi {
        private final Map<String, List<Integer>> path2changedValues;

        public DummyRevisionApi(Map<String, List<Integer>> path2changedValues) {
            this.path2changedValues = path2changedValues;
        }

        @Override
        public FileApi file(String path) {
            return getFileApi(path);
        }

        private FileApi getFileApi(final String path) {
            return new FileApi() {
                @Override
                public BinaryResult content() throws RestApiException {
                    throw new UnsupportedOperationException("This is a dummy test class");
                }

                @Override
                public DiffInfo diff() throws RestApiException {
                    return generateDiffInfoByPath(path);
                }

                @Override
                public DiffInfo diff(String base) throws RestApiException {
                    throw new UnsupportedOperationException("This is a dummy test class");
                }

                @Override
                public DiffInfo diff(int parent) throws RestApiException {
                    throw new UnsupportedOperationException("This is a dummy test class");
                }

                @Override
                public DiffRequest diffRequest() throws RestApiException {
                    throw new UnsupportedOperationException("This is a dummy test class");
                }

            };

        }

        private DiffInfo generateDiffInfoByPath(String path) {
            DiffInfo info = new DiffInfo();
            info.content = new ArrayList<DiffInfo.ContentEntry>();

            List<Integer> lines = path2changedValues.get(path);
            if (lines != null) {   // if file had been affected by change
                for (int v : lines) {
                    info.content.add(createContentEntry(lines.indexOf(v) % 2 != 0, v));
                }
            }
            return info;
        }

        private DiffInfo.ContentEntry createContentEntry(boolean changed, int countOfStrings) {
            DiffInfo.ContentEntry entry = new DiffInfo.ContentEntry();
            for (int i = 0; i < countOfStrings; i++) {
                String v = ((Integer) i).toString();
                if (changed) {
                    if (entry.a == null || entry.b == null) {
                        entry.a = new ArrayList<String>();
                        entry.b = new ArrayList<String>();
                    }
                    entry.a.add(v + v);
                    entry.b.add(v + v + v);
                } else {
                    if (entry.ab == null) {
                        entry.ab = new ArrayList<String>();
                    }
                    entry.ab.add(v);
                }
            }
            return entry;
        }

        @Override
        public void delete() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public void review(ReviewInput in) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public void submit() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public void submit(SubmitInput in) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public void publish() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public ChangeApi cherryPick(CherryPickInput in) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public ChangeApi rebase() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public ChangeApi rebase(RebaseInput in) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public boolean canRebase() {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public void setReviewed(String path, boolean reviewed) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Set<String> reviewed() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Map<String, FileInfo> files() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Map<String, FileInfo> files(String base) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public MergeableInfo mergeable() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public MergeableInfo mergeableOtherBranches() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Map<String, List<CommentInfo>> comments() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Map<String, List<CommentInfo>> drafts() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public DraftApi createDraft(DraftInput in) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public DraftApi draft(String id) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public CommentApi comment(String id) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Map<String, ActionInfo> actions() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public List<CommentInfo> commentsAsList() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public List<CommentInfo> draftsAsList() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Map<String, FileInfo> files(int parentNum) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public RevisionApi.MergeListRequest getMergeList() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public BinaryResult patch() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public BinaryResult patch(String path) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public RobotCommentApi robotComment(String id) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public Map<String, List<RobotCommentInfo>> robotComments() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public List<RobotCommentInfo> robotCommentsAsList() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public BinaryResult submitPreview() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public SubmitType submitType() throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

        @Override
        public SubmitType testSubmitType(TestSubmitRuleInput in) throws RestApiException {
            throw new UnsupportedOperationException("This is a dummy test class");
        }

    }

}
