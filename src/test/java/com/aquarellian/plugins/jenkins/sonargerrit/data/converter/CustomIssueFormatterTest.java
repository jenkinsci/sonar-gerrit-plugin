package com.aquarellian.plugins.jenkins.sonargerrit.data.converter;

import com.aquarellian.plugins.jenkins.sonargerrit.data.SonarReportBuilder;
import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Issue;
import com.aquarellian.plugins.jenkins.sonargerrit.data.entity.Report;
import hudson.FilePath;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 13:05
 * <p/>
 * $Id$
 */
public class CustomIssueFormatterTest {
    @Test
    public void testKnownUrl() throws IOException, InterruptedException, URISyntaxException {
        Issue i = getIssue();
        String text = "<severity> Sonar violation:\n\n\n<message>\n\n\nRead more: <rule_url>";
        String expectedResult = "MINOR Sonar violation:\n\n\nRemove this unused import 'com.magenta.guice.property.PropertiesHandler'.\n\n\nRead more: http://localhost:9000/coding_rules#rule_key=squid%3AUselessImportCheck";
        CustomIssueFormatter basicIssueConverter = new CustomIssueFormatter(i, text, "http://localhost:9000");
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
    }

    @Test
    public void testUnknownUrl() throws IOException, InterruptedException, URISyntaxException {
        Issue i = getIssue();
        String text = "<severity> Sonar violation:\n\n\n<message>\n\n\nRead more: <rule_url>";
        String expectedResult = "MINOR Sonar violation:\n\n\nRemove this unused import 'com.magenta.guice.property.PropertiesHandler'.\n\n\nRead more: squid:UselessImportCheck";
        CustomIssueFormatter basicIssueConverter = new CustomIssueFormatter(i, text, null);
        Assert.assertEquals(expectedResult, basicIssueConverter.getMessage());
    }

    private Issue getIssue() throws URISyntaxException, IOException, InterruptedException {
        URL url = getClass().getClassLoader().getResource("filter.json");

        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        Report rep = new SonarReportBuilder().fromJson(json);

        return rep.getIssues().get(0);
    }
}
