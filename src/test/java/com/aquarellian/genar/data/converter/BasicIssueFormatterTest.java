package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import hudson.FilePath;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 15.09.2015 16:57
 * <p/>
 * $Id$
 */
public class BasicIssueFormatterTest {
    @Test
    public void testKnownUrl() throws IOException, InterruptedException, URISyntaxException {
        Issue i = getIssue();
        BasicIssueFormatter basicIssueConverter = new BasicIssueFormatter(i, "http://localhost:9000");
        String message = basicIssueConverter.formatMessage();
        String severity = basicIssueConverter.formatSeverity();
        String rule = basicIssueConverter.formatRule();
        Assert.assertEquals("Remove this unused import 'com.magenta.guice.property.PropertiesHandler'.\n\n\n", message);
        Assert.assertEquals("MINOR Sonar violation:\n\n\n", severity);
        Assert.assertEquals("Read more: http://localhost:9000/coding_rules#q=squid%3AUselessImportCheck\n", rule);
    }

    @Test
    public void testUnknownUrl() throws IOException, InterruptedException, URISyntaxException {
        Issue i = getIssue();
        BasicIssueFormatter basicIssueConverter = new BasicIssueFormatter(i, null);
        String message = basicIssueConverter.formatMessage();
        String severity = basicIssueConverter.formatSeverity();
        String rule = basicIssueConverter.formatRule();
        Assert.assertEquals("Remove this unused import 'com.magenta.guice.property.PropertiesHandler'.\n\n\n", message);
        Assert.assertEquals("MINOR Sonar violation:\n\n\n", severity);
        Assert.assertEquals("Read more: squid:UselessImportCheck\n", rule);
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
