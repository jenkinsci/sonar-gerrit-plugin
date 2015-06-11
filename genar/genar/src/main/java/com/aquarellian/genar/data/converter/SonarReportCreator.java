package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.entity.SonarReportImpl;
import com.aquarellian.genar.data.entity.interfaces.SonarReport;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 17:07
 * <p/>
 * $Id$
 */
public class SonarReportCreator implements InstanceCreator<SonarReport> {
    public SonarReport createInstance(Type type) {
        return new SonarReportImpl();
    }
}
