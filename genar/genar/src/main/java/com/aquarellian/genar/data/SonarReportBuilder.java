package com.aquarellian.genar.data;

import com.aquarellian.genar.data.converter.*;
import com.aquarellian.genar.data.entity.SonarReportImpl;
import com.aquarellian.genar.data.entity.interfaces.*;
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
            .registerTypeAdapter(SonarReport.class, new SonarReportCreator())
            .registerTypeAdapter(SonarRule.class, new SonarRuleCreator())
            .registerTypeAdapter(SonarIssue.class, new SonarIssueCreator())
            .registerTypeAdapter(SonarComponent.class, new SonarComponentCreator())
            .registerTypeAdapter(SonarUser.class, new SonarUserCreator())
            .create();

    public SonarReport fromJson(String json) {
        return GSON.fromJson(json, SonarReportImpl.class);
    }



}
