package com.aquarellian.genar.jenkins;

import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import com.aquarellian.genar.data.entity.Severity;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gerrit.extensions.api.changes.*;
import com.google.gerrit.extensions.common.CommentInfo;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.common.MergeableInfo;
import com.google.gerrit.extensions.restapi.BinaryResult;
import com.google.gerrit.extensions.restapi.RestApiException;
import hudson.FilePath;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 30.08.2015 13:41
 * <p/>
 * $Id$
 */
public class SonarToGerritBuilderTest {

    @Test
    public void testFilterByPredicates() throws IOException, InterruptedException, URISyntaxException {
        Report report = readreport();
        Assert.assertEquals(19, report.getIssues().size());

        Iterable<Issue> issues = new SonarToGerritBuilder("", Severity.CRITICAL.name(), true).filterIssuesByPredicates(report);
        Assert.assertEquals(2, Sets.newHashSet(issues).size());

        issues = new SonarToGerritBuilder("", Severity.MAJOR.name(), true).filterIssuesByPredicates(report);
        Assert.assertEquals(12, Sets.newHashSet(issues).size());

        issues = new SonarToGerritBuilder("", Severity.INFO.name(), true).filterIssuesByPredicates(report);
        Assert.assertEquals(19, Sets.newHashSet(issues).size());

        issues = new SonarToGerritBuilder("", Severity.MINOR.name(), true).filterIssuesByPredicates(report);
        Assert.assertEquals(18, Sets.newHashSet(issues).size());

        issues = new SonarToGerritBuilder("", Severity.BLOCKER.name(), true).filterIssuesByPredicates(report);
        Assert.assertEquals(1, Sets.newHashSet(issues).size());
    }

    @Test
    public void testGenerateRealNameMap() throws InterruptedException, IOException, URISyntaxException {
        Report report = readreport();
        Assert.assertEquals(19, report.getIssues().size());
        Multimap<String, Issue> multimap = new SonarToGerritBuilder("", Severity.CRITICAL.name(), true).generateFilenameToIssuesMap(report, report.getIssues());

        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(8, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("guice-jpa/src/main/java/com/magenta/guice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("src/main/java/com/aquarellian/genar/ObjectHelper.java").size());

    }

    @Test
    public void testFilterIssuesByChangedLines() throws InterruptedException, IOException, URISyntaxException, RestApiException {
        Report report = readreport();
        Assert.assertEquals(19, report.getIssues().size());

        SonarToGerritBuilder builder = new SonarToGerritBuilder("", Severity.INFO.name(), true);
        Multimap<String, Issue> multimap = builder.generateFilenameToIssuesMap(report, report.getIssues());

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



        builder.filterIssuesByChangedLines(multimap, revApi);

        // list of lines commented by sonar : 37, 54,81, 99, 106, 108, 122, 162
        // list of lines affected by change : 37, 99, 106, 108
        Set<Integer> resultIssues = Sets.newHashSet(37, 99, 106, 108);

        Collection<Issue> issues = multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java");
        for (Issue  issue : issues) {
                Assert.assertTrue(resultIssues.contains(issue.getLine()));
        }

        issues = multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java");
        Assert.assertEquals(0, issues.size());
    }


    private Report readreport() throws IOException, InterruptedException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource("filter.json");

        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        return new SonarReportBuilder().fromJson(json);
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
    }

}
