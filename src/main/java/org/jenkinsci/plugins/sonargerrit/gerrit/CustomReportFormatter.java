package org.jenkinsci.plugins.sonargerrit.gerrit;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jenkinsci.plugins.sonargerrit.sonar.ByExactSeverityPredicate;
import org.jenkinsci.plugins.sonargerrit.sonar.ByMinSeverityPredicate;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueAdapter;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 16.09.2015 13:17 */
@Restricted(NoExternalUse.class)
class CustomReportFormatter {

  private final String successMessage;
  private final String failMessage;

  private final Iterable<IssueAdapter> issues;

  public CustomReportFormatter(
      Iterable<IssueAdapter> issues, String failMessage, String successMessage) {
    this.issues = issues;
    this.failMessage = failMessage;
    this.successMessage = successMessage;
  }

  public String getMessage() {
    String res = getSize(issues) > 0 ? failMessage : successMessage;
    for (Tag tag : Tag.values()) {
      res = res.replace(tag.getName(), getValueToReplace(tag));
    }
    return res;
  }

  private String getValueToReplace(Tag tag) {
    int value;
    switch (tag) {
      case INFO_ISSUE_COUNT:
      case MINOR_ISSUE_COUNT:
      case MAJOR_ISSUE_COUNT:
      case CRITICAL_ISSUE_COUNT:
      case BLOCKER_ISSUE_COUNT:
        value = getSize(filterByExactSeverityPredicate(tag.getSeverity()));
        return String.valueOf(value);
      case AT_LEAST_MINOR_ISSUE_COUNT:
      case AT_LEAST_MAJOR_ISSUE_COUNT:
      case AT_LEAST_CRITICAL_ISSUE_COUNT:
      case TOTAL_COUNT:
        value = getSize(filterByMinSeverityPredicate(tag.getSeverity()));
        return String.valueOf(value);
      default:
        return "";
    }
  }

  private int getSize(Iterable<?> i) {
    return Lists.newArrayList(i).size();
  }

  private Iterable<IssueAdapter> filterByExactSeverityPredicate(Severity s) {
    return Iterables.filter(issues, ByExactSeverityPredicate.apply(s));
  }

  private Iterable<IssueAdapter> filterByMinSeverityPredicate(Severity s) {
    return Iterables.filter(issues, ByMinSeverityPredicate.apply(s));
  }

  public enum Tag {
    INFO_ISSUE_COUNT("<info_count>", Severity.INFO),
    MINOR_ISSUE_COUNT("<minor_count>", Severity.MINOR),
    MAJOR_ISSUE_COUNT("<major_count>", Severity.MAJOR),
    CRITICAL_ISSUE_COUNT("<critical_count>", Severity.CRITICAL),
    BLOCKER_ISSUE_COUNT("<blocker_count>", Severity.BLOCKER),
    AT_LEAST_MINOR_ISSUE_COUNT("<min_minor_count>", Severity.MINOR),
    AT_LEAST_MAJOR_ISSUE_COUNT("<min_major_count>", Severity.MAJOR),
    AT_LEAST_CRITICAL_ISSUE_COUNT("<min_critical_count>", Severity.CRITICAL),
    TOTAL_COUNT("<total_count>", Severity.INFO);

    private final String name;
    private final Severity severity;

    Tag(String name, Severity severity) {
      this.name = name;
      this.severity = severity;
    }

    public String getName() {
      return name;
    }

    public Severity getSeverity() {
      return severity;
    }
  }
}
