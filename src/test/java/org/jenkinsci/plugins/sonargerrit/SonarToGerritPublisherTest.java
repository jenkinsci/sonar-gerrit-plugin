package org.jenkinsci.plugins.sonargerrit;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gerrit.extensions.api.changes.*;
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
 */
public class SonarToGerritPublisherTest {

    @Test
    public void testFilterByPredicates() throws IOException, InterruptedException, URISyntaxException {
        Report report = readreport();
        Assert.assertEquals(19, report.getIssues().size());

        // filter by severity predicate
        SonarToGerritPublisher publisher = buildPublisher(Severity.CRITICAL);
        Iterable<Issue> issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(2, Sets.newHashSet(issues).size());

        publisher = buildPublisher(Severity.MAJOR);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(12, Sets.newHashSet(issues).size());

        publisher = buildPublisher(Severity.INFO);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(19, Sets.newHashSet(issues).size());

        publisher = buildPublisher(Severity.MINOR);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(18, Sets.newHashSet(issues).size());

        publisher = buildPublisher(Severity.BLOCKER);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        // filter by severity + new issues only predicate
        publisher = buildPublisher(Severity.CRITICAL);
        publisher.setNewIssuesOnly(true);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(0, Sets.newHashSet(issues).size());

        publisher = buildPublisher(Severity.MAJOR);
        publisher.setNewIssuesOnly(true);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        publisher.setNewIssuesOnly(true);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        publisher = buildPublisher(Severity.MINOR);
        publisher.setNewIssuesOnly(true);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
        Assert.assertEquals(1, Sets.newHashSet(issues).size());

        publisher = buildPublisher(Severity.BLOCKER);
        publisher.setNewIssuesOnly(true);
        issues = publisher.filterIssuesByPredicates(report.getIssues());
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
        SonarToGerritPublisher sonarToGerritPublisher = buildPublisher(Severity.INFO);
        sonarToGerritPublisher.setSubJobConfigs(Arrays.asList(config1, config2));
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


        SonarToGerritPublisher publisher = buildPublisher(Severity.INFO);
        publisher.filterIssuesByChangedLines(multimap, revApi);

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
        SonarToGerritPublisher publisher = buildPublisher(Severity.INFO);
        ReviewInput reviewResult = publisher.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(-1, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.OWNER, reviewResult.notify);

        publisher = buildPublisher(Severity.INFO);
        publisher.setPostScore(false);
        publisher.setNoIssuesNotification(null);
        publisher.setIssuesNotification(null);
        reviewResult = publisher.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(null, reviewResult.labels);
        Assert.assertEquals(NotifyHandling.OWNER, reviewResult.notify);

        publisher = buildPublisher(Severity.INFO);
        publisher.setNoIssuesScore("0");
        publisher.setIssuesScore("0");
        publisher.setNoIssuesNotification(null);
        publisher.setIssuesNotification(null);

        reviewResult = publisher.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(0, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.OWNER, reviewResult.notify);

        publisher = buildPublisher(Severity.INFO);
        publisher.setNoIssuesScore("1test");
        publisher.setIssuesScore("-1test");
        publisher.setNoIssuesNotification("NONE");
        publisher.setIssuesNotification("ALL");

        reviewResult = publisher.getReviewResult(finalIssues);
        Assert.assertEquals("Some Issues Header", reviewResult.message);
        Assert.assertEquals(1, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(0, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.ALL, reviewResult.notify);

        publisher = buildPublisher(Severity.INFO);
        publisher.setNoIssuesNotification(null);
        publisher.setIssuesNotification(null);

        finalIssues = LinkedListMultimap.create();
        reviewResult = publisher.getReviewResult(finalIssues);
        Assert.assertEquals("No Issues Header", reviewResult.message);
        Assert.assertEquals(0, reviewResult.comments.size());
        Assert.assertEquals(1, reviewResult.labels.size());
        Assert.assertEquals(+1, reviewResult.labels.get("Test").intValue());
        Assert.assertEquals(NotifyHandling.NONE, reviewResult.notify);

        publisher = buildPublisher(Severity.INFO);
        publisher.setNoIssuesNotification("OWNER_REVIEWERS");
        publisher.setIssuesNotification("ALL");
        finalIssues = LinkedListMultimap.create();
        reviewResult = publisher.getReviewResult(finalIssues);
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

    private SonarToGerritPublisher buildPublisher(Severity severity) {
        SonarToGerritPublisher publisher = new SonarToGerritPublisher();
        publisher.setSonarURL("");
        publisher.setSubJobConfigs(null);
        publisher.setSeverity(severity.name());
        publisher.setChangedLinesOnly(true);
        publisher.setNewIssuesOnly(false);
        publisher.setNoIssuesToPostText("No Issues Header");
        publisher.setSomeIssuesToPostText("Some Issues Header");
        publisher.setIssueComment("Issue Comment");
        publisher.setOverrideCredentials(false);
        publisher.setHttpUsername("");
        publisher.setHttpPassword("");
        publisher.setPostScore(true);
        publisher.setCategory("Test");
        publisher.setNoIssuesScore("+1");
        publisher.setIssuesScore("-1");
        publisher.setNoIssuesNotification("NONE");
        publisher.setIssuesNotification("OWNER");
        return publisher;
    }

}
