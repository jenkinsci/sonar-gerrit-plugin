package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import com.google.gson.*;
import java.util.Date;
import org.jenkinsci.plugins.sonargerrit.inspection.converter.DateTypeConverter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
@Restricted(NoExternalUse.class)
public class SonarReportBuilder {
  private final Gson GSON =
      new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeConverter()).create();

  public Report fromJson(String json) {
    return GSON.fromJson(json, Report.class);
  }
}
