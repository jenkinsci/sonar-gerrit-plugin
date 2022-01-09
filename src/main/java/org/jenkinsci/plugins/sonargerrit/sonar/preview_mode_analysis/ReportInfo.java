package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

/** @author Réda Housni Alaoui */
class ReportInfo {

  public final SubJobConfig config;
  public final ReportRepresentation report;

  public ReportInfo(SubJobConfig config, ReportRepresentation report) {
    this.config = config;
    this.report = report;
  }
}
