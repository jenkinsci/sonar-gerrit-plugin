package org.jenkinsci.plugins.sonargerrit.inspection;

import hudson.FilePath;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.*;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarReportBuilder;
import org.junit.Assert;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 30.11.2017 15:32
 * <p>
 * $Id$
 */
public class ReportDataChecker {
    public static void checkFile(String filename, Report rep) {
        switch (filename) {
            case "one_issue.json":
                checkOneIssueFile(rep);
                break;
            case "sc-rep1":
                checkSCRep1(rep);
                break;
            case "sc-rep2":
                checkSCRep2(rep);
                break;
            case "sc-rep3":
                checkSCRep3(rep);
                break;

        }

    }

    private static void checkReport1File(Report rep) {
        Assert.assertNotNull(rep);
        Assert.assertNotNull(rep.getComponents());
        Assert.assertNotNull(rep.getIssues());
        Assert.assertNotNull(rep.getRules());
        Assert.assertNotNull(rep.getUsers());
        Assert.assertEquals(8, rep.getIssues().size());
        Assert.assertEquals(11, rep.getComponents().size());
        Assert.assertEquals(0, rep.getRules().size());
        Assert.assertEquals(0, rep.getUsers().size());

        // issues ------------------------------------

        Issue i1 = rep.getIssues().get(0);
        Assert.assertEquals("c48d7f88-64bb-45ec-b64d-b5a536384183", i1.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
                i1.getComponent());
        Assert.assertEquals(5, i1.getLine().intValue());


        Issue i2 = rep.getIssues().get(0);
        Assert.assertEquals("6b8a2d04-e126-481d-83ef-5719e3c470ea", i2.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-jpa:src/main/java/com/turquoise/juice/jpa/DBInterceptor.java",
                i2.getComponent());
        Assert.assertEquals(228, i2.getLine().intValue());

        Issue i3 = rep.getIssues().get(0);
        Assert.assertEquals("993137c8-42b4-4774-ad54-7c902a8d4054", i3.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-jpa:src/main/java/com/turquoise/juice/jpa/DBInterceptor.java",
                i3.getComponent());
        Assert.assertEquals(282, i3.getLine().intValue());

        Issue i4 = rep.getIssues().get(0);
        Assert.assertEquals("43bd8fdd-e5d2-46e6-add1-e3b27e9d8d26", i4.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
                i4.getComponent());
        Assert.assertEquals(106, i4.getLine().intValue());

        Issue i5 = rep.getIssues().get(0);
        Assert.assertEquals("4b0ca769-7ff4-4e2f-9440-86e504ec7785", i5.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
                i5.getComponent());
        Assert.assertEquals(54, i5.getLine().intValue());

        Issue i6 = rep.getIssues().get(0);
        Assert.assertEquals("50bcd645-f888-4813-9d1d-3d72403e2057", i6.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
                i6.getComponent());
        Assert.assertEquals(122, i6.getLine().intValue());

        Issue i7 = rep.getIssues().get(0);
        Assert.assertEquals("60eec0ad-0b69-4b4c-89f8-8624abc9f42c", i7.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
                i7.getComponent());
        Assert.assertEquals(81, i7.getLine().intValue());

        Issue i8 = rep.getIssues().get(0);
        Assert.assertEquals("78f1504b-040d-496a-bb35-be7afbd802e8", i8.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/PluginsManager.java",
                i8.getComponent());
        Assert.assertEquals(37, i8.getLine().intValue());

        // components ----------------
        Component c1 = rep.getComponents().get(0);
        Assert.assertEquals("com.megaproject.juice:juice-jpa", c1.getKey());
        Assert.assertEquals("juice-jpa", c1.getPath());

        Component c2 = rep.getComponents().get(1);
        Assert.assertEquals("com.megaproject.juice:juice-events", c2.getKey());
        Assert.assertEquals("juice-events", c2.getPath());

        Component c3 = rep.getComponents().get(2);
        Assert.assertEquals("com.megaproject.juice:juice-bootstrap", c2.getKey());
        Assert.assertEquals("juice-bootstrap", c2.getPath());

        Component c4 = rep.getComponents().get(3);
        Assert.assertEquals("com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-bootstrap", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

        Component c5 = rep.getComponents().get(4);
        Assert.assertEquals("com.megaproject.juice:juice-jpa:src/main/java/com/turquoise/juice/jpa/DBInterceptor.java.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/jpa/DBInterceptor.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-jpa", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

        Component c6 = rep.getComponents().get(5);
        Assert.assertEquals("com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

        Component c7 = rep.getComponents().get(6);
        Assert.assertEquals("com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

        Component c8 = rep.getComponents().get(7);
        Assert.assertEquals("com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

        Component c9 = rep.getComponents().get(8);
        Assert.assertEquals("com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

        Component c10 = rep.getComponents().get(9);
        Assert.assertEquals("com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());

        Component c11 = rep.getComponents().get(10);
        Assert.assertEquals("com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());
    }

    private static void checkOneIssueFile(Report rep) {
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
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
                i.getComponent());
        Assert.assertEquals(5, i.getLine().intValue());
        Assert.assertEquals("Remove this unused import 'com.turquoise.juice.property.PropertiesHandler'.", i.getMessage());
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
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c1.getKey());
        Assert.assertEquals("juice-mbean", c1.getPath());

        Component c2 = rep.getComponents().get(1);
        Assert.assertEquals("com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());
    }

    private static void checkSCRep1(Report rep){
        Assert.assertNotNull(rep);
        Assert.assertNotNull(rep.getComponents());
        Assert.assertNotNull(rep.getIssues());
        Assert.assertNotNull(rep.getRules());
        Assert.assertNotNull(rep.getUsers());
        Assert.assertEquals(2, rep.getComponents().size());
        Assert.assertEquals(1, rep.getIssues().size());
        Assert.assertEquals(0, rep.getRules().size());
        Assert.assertEquals(0, rep.getUsers().size());

        Issue i = rep.getIssues().get(0);
        Assert.assertEquals("c48d7f88-64bb-45ec-b64d-b5a536384183", i.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
                i.getComponent());
        Assert.assertEquals(5, i.getLine().intValue());
        Assert.assertEquals("Remove this unused import 'com.turquoise.juice.property.PropertiesHandler'.", i.getMessage());
        Assert.assertEquals(Severity.MINOR, i.getSeverity());
        Assert.assertEquals("squid:UselessImportCheck", i.getRule());
        Assert.assertEquals("OPEN", i.getStatus());
        Assert.assertEquals(false, i.isNew());

        Component c1 = rep.getComponents().get(0);
        Assert.assertEquals("com.megaproject.juice:juice-bootstrap", c1.getKey());
        Assert.assertEquals("juice-bootstrap", c1.getPath());

        Component c2 = rep.getComponents().get(1);
        Assert.assertEquals("com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-bootstrap", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());
    }

    private static  void checkSCRep2(Report rep){
        Assert.assertNotNull(rep);
        Assert.assertNotNull(rep.getComponents());
        Assert.assertNotNull(rep.getIssues());
        Assert.assertNotNull(rep.getRules());
        Assert.assertNotNull(rep.getUsers());
        Assert.assertEquals(2, rep.getComponents().size());
        Assert.assertEquals(1, rep.getIssues().size());
        Assert.assertEquals(0, rep.getRules().size());
        Assert.assertEquals(0, rep.getUsers().size());

        Issue i = rep.getIssues().get(0);
        Assert.assertEquals("6b8a2d04-e126-481d-83ef-5719e3c470ea", i.getKey());
        Assert.assertEquals(
                "com.megaproject.juice:juice-jpa:src/main/java/com/turquoise/juice/jpa/DBInterceptor.java",
                i.getComponent());
        Assert.assertEquals(228, i.getLine().intValue());
        Assert.assertEquals("Do not forget to remove this deprecated code someday.", i.getMessage());
        Assert.assertEquals(Severity.INFO, i.getSeverity());
        Assert.assertEquals("squid:S1133", i.getRule());
        Assert.assertEquals("OPEN", i.getStatus());
        Assert.assertEquals(false, i.isNew());

        Component c1 = rep.getComponents().get(0);
        Assert.assertEquals("com.megaproject.juice:juice-jpa", c1.getKey());
        Assert.assertEquals("juice-jpa", c1.getPath());

        Component c2 = rep.getComponents().get(1);
        Assert.assertEquals("com.megaproject.juice:juice-jpa:src/main/java/com/turquoise/juice/jpa/DBInterceptor.java", c2.getKey());
        Assert.assertEquals("src/main/java/com/turquoise/juice/jpa/DBInterceptor.java", c2.getPath());
        Assert.assertEquals("com.megaproject.juice:juice-jpa", c2.getModuleKey());
        Assert.assertEquals("SAME", c2.getStatus());
    }

    private static void checkSCRep3(Report rep){
        Assert.assertNotNull(rep);
        Assert.assertNotNull(rep.getComponents());
        Assert.assertNotNull(rep.getIssues());
        Assert.assertNotNull(rep.getRules());
        Assert.assertNotNull(rep.getUsers());
        Assert.assertEquals(1, rep.getComponents().size());
        Assert.assertEquals(1, rep.getIssues().size());
        Assert.assertEquals(0, rep.getRules().size());
        Assert.assertEquals(0, rep.getUsers().size());

        Issue i = rep.getIssues().get(0);
        Assert.assertEquals("81c5c4f8-08c9-4340-861e-8491c9f4666a", i.getKey());
        Assert.assertEquals(
                "com.aquarellian:sonar-gerrit:src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java",
                i.getComponent());
        Assert.assertEquals(18, i.getLine().intValue());
        Assert.assertEquals("Add a private constructor to hide the implicit public one.", i.getMessage());
        Assert.assertEquals(Severity.MAJOR, i.getSeverity());
        Assert.assertEquals("squid:S1118", i.getRule());
        Assert.assertEquals("OPEN", i.getStatus());
        Assert.assertEquals(true, i.isNew());

        Component c1 = rep.getComponents().get(0);
        Assert.assertEquals("com.aquarellian:sonar-gerrit:src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java", c1.getKey());
        Assert.assertEquals("src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java", c1.getPath());
        Assert.assertEquals("com.aquarellian:sonar-gerrit", c1.getModuleKey());
        Assert.assertEquals("ADDED", c1.getStatus());
    }
}
