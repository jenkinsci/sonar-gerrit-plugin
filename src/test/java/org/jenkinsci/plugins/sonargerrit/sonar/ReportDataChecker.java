package org.jenkinsci.plugins.sonargerrit.sonar;

import java.util.Calendar;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 30.11.2017 15:32
 *
 * <p>$Id$
 */
public class ReportDataChecker {
  public static void checkFile(String filename, ReportRepresentation rep) {
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

  private static void checkOneIssueFile(ReportRepresentation rep) {
    Assertions.assertNotNull(rep);
    Assertions.assertNotNull(rep.getComponents());
    Assertions.assertNotNull(rep.getIssues());
    Assertions.assertEquals(2, rep.getComponents().size());
    Assertions.assertEquals(1, rep.getIssues().size());

    IssueRepresentation i = rep.getIssues().get(0);
    Assertions.assertEquals("c48d7f88-64bb-45ec-b64d-b5a536384183", i.getKey());
    Assertions.assertEquals(
        "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
        i.getComponent());
    Assertions.assertEquals(5, i.getLine().intValue());
    Assertions.assertEquals(
        "Remove this unused import 'com.turquoise.juice.property.PropertiesHandler'.",
        i.getMessage());
    Assertions.assertEquals(Severity.MINOR, i.getSeverity());
    Assertions.assertEquals("squid:UselessImportCheck", i.getRule());
    Assertions.assertEquals("OPEN", i.getStatus());
    Assertions.assertFalse(i.isNew());
    Calendar c = Calendar.getInstance();
    c.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
    c.set(2015, Calendar.MAY, 10, 0, 46, 9);
    c.set(Calendar.MILLISECOND, 0);
    Assertions.assertEquals(c.getTime(), i.getCreationDate());

    ComponentRepresentation c1 = rep.getComponents().get(0);
    Assertions.assertEquals("com.megaproject.juice:juice-mbean", c1.getKey());
    Assertions.assertEquals("juice-mbean", c1.getPath());

    ComponentRepresentation c2 = rep.getComponents().get(1);
    Assertions.assertEquals(
        "com.megaproject.juice:juice-mbean:src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java",
        c2.getKey());
    Assertions.assertEquals(
        "src/main/java/com/turquoise/juice/mbean/MBeanManagerModule.java", c2.getPath());
    Assertions.assertEquals("com.megaproject.juice:juice-mbean", c2.getModuleKey());
    Assertions.assertEquals("SAME", c2.getStatus());
  }

  private static void checkSCRep1(ReportRepresentation rep) {
    Assertions.assertNotNull(rep);
    Assertions.assertNotNull(rep.getComponents());
    Assertions.assertNotNull(rep.getIssues());
    Assertions.assertEquals(2, rep.getComponents().size());
    Assertions.assertEquals(1, rep.getIssues().size());

    IssueRepresentation i = rep.getIssues().get(0);
    Assertions.assertEquals("c48d7f88-64bb-45ec-b64d-b5a536384183", i.getKey());
    Assertions.assertEquals(
        "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
        i.getComponent());
    Assertions.assertEquals(5, i.getLine().intValue());
    Assertions.assertEquals(
        "Remove this unused import 'com.turquoise.juice.property.PropertiesHandler'.",
        i.getMessage());
    Assertions.assertEquals(Severity.MINOR, i.getSeverity());
    Assertions.assertEquals("squid:UselessImportCheck", i.getRule());
    Assertions.assertEquals("OPEN", i.getStatus());
    Assertions.assertFalse(i.isNew());

    ComponentRepresentation c1 = rep.getComponents().get(0);
    Assertions.assertEquals("com.megaproject.juice:juice-bootstrap", c1.getKey());
    Assertions.assertEquals("juice-bootstrap", c1.getPath());

    ComponentRepresentation c2 = rep.getComponents().get(1);
    Assertions.assertEquals(
        "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
        c2.getKey());
    Assertions.assertEquals(
        "src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java", c2.getPath());
    Assertions.assertEquals("com.megaproject.juice:juice-bootstrap", c2.getModuleKey());
    Assertions.assertEquals("SAME", c2.getStatus());
  }

  private static void checkSCRep2(ReportRepresentation rep) {
    Assertions.assertNotNull(rep);
    Assertions.assertNotNull(rep.getComponents());
    Assertions.assertNotNull(rep.getIssues());
    Assertions.assertEquals(2, rep.getComponents().size());
    Assertions.assertEquals(1, rep.getIssues().size());

    IssueRepresentation i = rep.getIssues().get(0);
    Assertions.assertEquals("6b8a2d04-e126-481d-83ef-5719e3c470ea", i.getKey());
    Assertions.assertEquals(
        "com.megaproject.juice:juice-jpa:src/main/java/com/turquoise/juice/jpa/DBInterceptor.java",
        i.getComponent());
    Assertions.assertEquals(228, i.getLine().intValue());
    Assertions.assertEquals(
        "Do not forget to remove this deprecated code someday.", i.getMessage());
    Assertions.assertEquals(Severity.INFO, i.getSeverity());
    Assertions.assertEquals("squid:S1133", i.getRule());
    Assertions.assertEquals("OPEN", i.getStatus());
    Assertions.assertFalse(i.isNew());

    ComponentRepresentation c1 = rep.getComponents().get(0);
    Assertions.assertEquals("com.megaproject.juice:juice-jpa", c1.getKey());
    Assertions.assertEquals("juice-jpa", c1.getPath());

    ComponentRepresentation c2 = rep.getComponents().get(1);
    Assertions.assertEquals(
        "com.megaproject.juice:juice-jpa:src/main/java/com/turquoise/juice/jpa/DBInterceptor.java",
        c2.getKey());
    Assertions.assertEquals(
        "src/main/java/com/turquoise/juice/jpa/DBInterceptor.java", c2.getPath());
    Assertions.assertEquals("com.megaproject.juice:juice-jpa", c2.getModuleKey());
    Assertions.assertEquals("SAME", c2.getStatus());
  }

  private static void checkSCRep3(ReportRepresentation rep) {
    Assertions.assertNotNull(rep);
    Assertions.assertNotNull(rep.getComponents());
    Assertions.assertNotNull(rep.getIssues());
    Assertions.assertEquals(1, rep.getComponents().size());
    Assertions.assertEquals(1, rep.getIssues().size());

    IssueRepresentation i = rep.getIssues().get(0);
    Assertions.assertEquals("81c5c4f8-08c9-4340-861e-8491c9f4666a", i.getKey());
    Assertions.assertEquals(
        "com.aquarellian:sonar-gerrit:src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java",
        i.getComponent());
    Assertions.assertEquals(18, i.getLine().intValue());
    Assertions.assertEquals(
        "Add a private constructor to hide the implicit public one.", i.getMessage());
    Assertions.assertEquals(Severity.MAJOR, i.getSeverity());
    Assertions.assertEquals("squid:S1118", i.getRule());
    Assertions.assertEquals("OPEN", i.getStatus());
    Assertions.assertTrue(i.isNew());

    ComponentRepresentation c1 = rep.getComponents().get(0);
    Assertions.assertEquals(
        "com.aquarellian:sonar-gerrit:src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java",
        c1.getKey());
    Assertions.assertEquals(
        "src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java", c1.getPath());
    Assertions.assertEquals("com.aquarellian:sonar-gerrit", c1.getModuleKey());
    Assertions.assertEquals("ADDED", c1.getStatus());
  }
}
