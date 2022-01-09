package org.jenkinsci.plugins.sonargerrit.sonar;

import java.util.ArrayList;
import java.util.List;

/** @author Réda Housni Alaoui */
public class Reports {

  private Reports() {}

  public static List<Issue> loadIssues(String json) {
    ReportRepresentation rep = new SonarReportBuilder().fromJson(json);

    List<Issue> adapters = new ArrayList<>();
    for (IssueRepresentation issue : rep.getIssues()) {
      adapters.add(new SimpleIssue(issue, null, new SubJobConfig(), null));
    }
    return adapters;
  }

  public static Issue loadFirstIssue(String json, String sonarQubeUrl) {
    ReportRepresentation rep = new SonarReportBuilder().fromJson(json);

    return new SimpleIssue(rep.getIssues().get(0), null, new SubJobConfig(), sonarQubeUrl);
  }
}
