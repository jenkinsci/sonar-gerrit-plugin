package com.aquarellian.genar.data;

import com.aquarellian.genar.data.converter.DateTypeConverter;
import com.aquarellian.genar.data.entity.Report;
import com.google.gson.*;

import java.util.Date;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 16:52
 * <p/>
 * $Id$
 */
public class SonarReportBuilder {
    private final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeConverter())
            .create();

    public Report fromJson(String json) {
        return GSON.fromJson(json, Report.class);
    }



}
