package com.aquarellian.genar.data;

import com.aquarellian.genar.data.converter.DateTypeConverter;
import com.aquarellian.genar.data.entity.Report;
import com.google.gson.*;

import java.util.Date;

/**
 * @author Tatiana Didik (aquarellian@gmail.com)
 */
public class SonarReportBuilder {
    private final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeConverter())
            .create();

    public Report fromJson(String json) {
        return GSON.fromJson(json, Report.class);
    }

}
