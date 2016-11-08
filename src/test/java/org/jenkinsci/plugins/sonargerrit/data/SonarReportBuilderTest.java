package org.jenkinsci.plugins.sonargerrit.data;

import hudson.FilePath;
import org.jenkinsci.plugins.sonargerrit.data.entity.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 12.06.2015 13:52
 *
 */
public class SonarReportBuilderTest {

    @Test
    public void testLargeFile() throws URISyntaxException, IOException, InterruptedException {
        URL url = getClass().getClassLoader().getResource("example.json");

        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        Report rep = new SonarReportBuilder().fromJson(json);
        Assert.assertNotNull(rep);
        Assert.assertNotNull(rep.getComponents());
        Assert.assertNotNull(rep.getIssues());
        Assert.assertNotNull(rep.getRules());
        Assert.assertNotNull(rep.getUsers());
        Assert.assertEquals(169, rep.getComponents().size());
        Assert.assertEquals(177, rep.getIssues().size());
        Assert.assertEquals(27, rep.getRules().size());
        Assert.assertEquals(0, rep.getUsers().size());
    }

    @Test
    public void testSmallFile() throws URISyntaxException, IOException, InterruptedException {
        URL url = getClass().getClassLoader().getResource("one_issue.json");
        File path = new File(url.toURI());
        FilePath filePath = new FilePath(path);
        String json = filePath.readToString();
        Report rep = new SonarReportBuilder().fromJson(json);
        Assert.assertNotNull(rep);
        Assert.assertNotNull(rep.getComponents());
        Assert.assertNotNull(rep.getIssues());
        Assert.assertNotNull(rep.getRules());
        Assert.assertNotNull(rep.getUsers());
        Assert.assertEquals(2, rep.getComponents().size());
        Assert.assertEquals(1, rep.getIssues().size());
        Assert.assertEquals(1, rep.getRules().size());
        Assert.assertEquals(0, rep.getUsers().size());

        Rule r = rep.getRules().get(0);
        Assert.assertEquals("squid:ModifiersOrderCheck", r.getKey());
        Assert.assertEquals("ModifiersOrderCheck", r.getRule());
        Assert.assertEquals("squid", r.getRepository());
        Assert.assertEquals("Modifiers should be declared in the correct order", r.getName());

        Issue i = rep.getIssues().get(0);
        Assert.assertEquals("c48d7f88-64bb-45ec-b64d-b5a536384183", i.getKey());
        Assert.assertEquals(
                "com.maxifier.guice:guice-bootstrap:src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java",
                i.getComponent());
        Assert.assertEquals(5, i.getLine().intValue());
        Assert.assertEquals("Remove this unused import 'com.magenta.guice.property.PropertiesHandler'.", i.getMessage());
        Assert.assertEquals(Severity.MINOR, i.getSeverity());
        Assert.assertEquals("squid:UselessImportCheck", i.getRule());
        Assert.assertEquals("OPEN", i.getStatus());
        Assert.assertEquals(false, i.isNew());
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
        c.set(2015, Calendar.MAY, 10, 0, 46, 9);
        c.set(Calendar.MILLISECOND, 0);
        Assert.assertEquals(c.getTime(), i.getCreationDate());

        Component c1 = rep.getComponents().get(0);
        Assert.assertEquals("com.maxifier.guice:guice-mbean", c1.getKey());
        Assert.assertEquals("guice-mbean", c1.getPath());

        Component c2 = rep.getComponents().get(1);
        Assert.assertEquals("com.maxifier.guice:guice-mbean:src/main/java/com/magenta/guice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/magenta/guice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.maxifier.guice:guice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

    }
}
