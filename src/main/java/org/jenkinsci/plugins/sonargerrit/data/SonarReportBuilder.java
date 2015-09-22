package org.jenkinsci.plugins.sonargerrit.data;

import org.jenkinsci.plugins.sonargerrit.data.converter.DateTypeConverter;
import org.jenkinsci.plugins.sonargerrit.data.entity.Report;
import com.google.gson.*;

import java.util.Date;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */
public class SonarReportBuilder {
    private final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeConverter())
            .create();

    public Report fromJson(String json) {
        return GSON.fromJson(json, Report.class);
    }

}
