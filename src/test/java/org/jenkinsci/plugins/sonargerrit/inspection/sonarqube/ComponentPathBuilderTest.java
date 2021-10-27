package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.common.collect.Multimap;
import hudson.FilePath;
import junit.framework.Assert;
import org.jenkinsci.plugins.sonargerrit.config.InspectionConfig;
import org.jenkinsci.plugins.sonargerrit.config.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 30.11.2017 18:26
 * <p>
 * $Id$
 */
public class ComponentPathBuilderTest {
    @Test
    public void testNestedComponent() throws IOException, InterruptedException {
        String filename = "sc-rep1.json";
        SubJobConfig config = createConfig("", filename);

        SonarConnector connector = readSonarReport(config);
        List<IssueAdapter> issues = connector.getIssues();
        Assert.assertEquals(1, issues.size());
        IssueAdapter issue = issues.get(0);

        Report report = connector.getRawReport(config);
        ComponentPathBuilder builder = new ComponentPathBuilder(report.getComponents());
        String issueComponent = issue.getComponent();
        String realFileName = builder
                .buildPrefixedPathForComponentWithKey(issueComponent, config.getProjectPath())
                .or(issueComponent);
        Assert.assertEquals("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java", realFileName);

        Assert.assertEquals("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java", issue.getFilepath()); // logic of another class - todo move out there
    }

    @Test
    public void testSuperNestedComponent() throws IOException, InterruptedException {
        String filename = "report3_with-nested-subprojects.json";
        SubJobConfig config = createConfig("testfolder", filename);
        SonarConnector connector = readSonarReport(config);
        List<IssueAdapter> issues = connector.getIssues();
        Assert.assertEquals(8, issues.size());

        Report report = connector.getRawReport(config);
        ComponentPathBuilder builder = new ComponentPathBuilder(report.getComponents());
        testIssue(builder, issues.get(0), config.getProjectPath(), "testfolder/base/core/proj1/src/main/java/proj1/Proj1.java");
        testIssue(builder, issues.get(1), config.getProjectPath(), "testfolder/base/core/proj1/src/main/java/proj1/Proj1.java");
        testIssue(builder, issues.get(2), config.getProjectPath(), "testfolder/base/core/proj1/src/main/java/proj1/Proj1.java");
        testIssue(builder, issues.get(3), config.getProjectPath(), "testfolder/base/core/proj2/src/main/java/com/proj2/Proj2.java");
        testIssue(builder, issues.get(4), config.getProjectPath(), "testfolder/base/core/proj2/sub2/src/main/java/com/proj2/sub2/SubProj2.java");
        testIssue(builder, issues.get(5), config.getProjectPath(), "testfolder/base/core/proj2/sub2/sub22/sub2222/sub22222/src/main/java/com/proj2/sub2/SubProj22222.java");
        testIssue(builder, issues.get(6), config.getProjectPath(), "testfolder/base/com.acme.util/src/main/java/com/acme/util/Util.java");
        testIssue(builder, issues.get(7), config.getProjectPath(), "testfolder/com.acme.app/src/main/java/com/acme/app/App.java");

        Multimap<String, IssueAdapter> multimap = connector.getReportData();
        Assert.assertEquals(8, multimap.size());
        Assert.assertEquals(3, multimap.get("testfolder/base/core/proj1/src/main/java/proj1/Proj1.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/core/proj2/sub2/src/main/java/com/proj2/sub2/SubProj2.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/core/proj2/sub2/sub22/sub2222/sub22222/src/main/java/com/proj2/sub2/SubProj22222.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/core/proj2/src/main/java/com/proj2/Proj2.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/base/com.acme.util/src/main/java/com/acme/util/Util.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/com.acme.app/src/main/java/com/acme/app/App.java").size());
    }

    @Test
    public void testEmptyProjectPath() throws IOException, InterruptedException {
        String filename = "filter.json";
        SubJobConfig config = createConfig("", filename);

        SonarConnector connector = readSonarReport(config);
        List<IssueAdapter> issues = connector.getIssues();
        Assert.assertEquals(19, issues.size());

        Multimap<String, IssueAdapter> multimap = connector.getReportData();
        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(8, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("juice-jpa/src/main/java/com/turquoise/juice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("juice-events/src/main/java/com/turquoise/juice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("juice-events/src/main/java/com/turquoise/juice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("juice-events/src/main/java/com/turquoise/juice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());
    }

    @Test
    public void testSimpleProjectPath() throws IOException, InterruptedException {
        String filename = "filter.json";
        SubJobConfig config = createConfig("testfolder", filename);

        SonarConnector connector = readSonarReport(config);
        List<IssueAdapter> issues = connector.getIssues();
        Assert.assertEquals(19, issues.size());

        Multimap<String, IssueAdapter> multimap = connector.getReportData();
        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(8, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("testfolder/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("testfolder/juice-jpa/src/main/java/com/turquoise/juice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("testfolder/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("testfolder/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/juice-events/src/main/java/com/turquoise/juice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/juice-events/src/main/java/com/turquoise/juice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/juice-events/src/main/java/com/turquoise/juice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder/src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());

    }

    @Test
    public void testTwoProjectPaths() throws IOException, InterruptedException {
        SubJobConfig config1 = createConfig("testfolder1", "report1.json");
        SubJobConfig config2 = createConfig("testfolder2", "report2.json");
        SonarConnector connector = readSonarReport(config1, config2);
        List<IssueAdapter> issues = connector.getIssues();
        Assert.assertEquals(19, issues.size());

        Multimap<String, IssueAdapter> multimap = connector.getReportData();
        Assert.assertEquals(19, multimap.size());
        Assert.assertEquals(9, multimap.keySet().size());
        Assert.assertEquals(1, multimap.get("testfolder1/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java").size());
        Assert.assertEquals(2, multimap.get("testfolder1/juice-jpa/src/main/java/com/turquoise/juice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(5, multimap.get("testfolder1/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(3, multimap.get("testfolder2/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("testfolder2/juice-bootstrap/src/main/java/com/turquoise/juice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/juice-events/src/main/java/com/turquoise/juice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/juice-events/src/main/java/com/turquoise/juice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/juice-events/src/main/java/com/turquoise/juice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("testfolder2/src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());
    }

    private void testIssue(ComponentPathBuilder builder, IssueAdapter issue, String projectPath, String expectedFilename) {
        String issueComponent = issue.getComponent();
        String realFileName = builder
                .buildPrefixedPathForComponentWithKey(issueComponent, projectPath)
                .or(issueComponent);
        Assert.assertEquals(expectedFilename, realFileName);

        Assert.assertEquals(expectedFilename, issue.getFilepath());
    }

    protected SonarConnector readSonarReport(SubJobConfig... configs) throws IOException, InterruptedException {
        SonarConnector connector = new SonarConnector(null, null, buildInspectionConfig(configs));
        connector.readSonarReports(new FilePath(new File("")));
        return connector;
    }

    private InspectionConfig buildInspectionConfig(SubJobConfig... configs){
        InspectionConfig config = new InspectionConfig();
        config.setType(InspectionConfig.DescriptorImpl.MULTI_TYPE);
        config.setSubJobConfigs(Arrays.asList(configs));
        return config;
    }

    private SubJobConfig createConfig(String ppath, String spath) {
        return new SubJobConfig(ppath, RESOURCES_PATH_PREFIX + spath);
    }

public static final String RESOURCES_PATH_PREFIX = "src/test/resources/";
}
