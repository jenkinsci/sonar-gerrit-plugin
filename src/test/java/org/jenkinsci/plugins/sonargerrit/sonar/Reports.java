package org.jenkinsci.plugins.sonargerrit.sonar;

import java.util.ArrayList;
import java.util.List;

/** @author Réda Housni Alaoui */
public class Reports {

  private Reports() {}

  public static List<IssueAdapter> loadIssues(String json) {
    Report rep = new SonarReportBuilder().fromJson(json);

    List<IssueAdapter> adapters = new ArrayList<>();
    for (Issue issue : rep.getIssues()) {
      adapters.add(new SonarQubeIssue(issue, null, new SubJobConfig()));
    }
    return adapters;
  }

  public static IssueAdapter loadFirstIssue(String json) {
    Report rep = new SonarReportBuilder().fromJson(json);

    return new SonarQubeIssue(rep.getIssues().get(0), null, new SubJobConfig());
  }
}
