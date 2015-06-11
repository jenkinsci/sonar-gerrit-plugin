package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.entity.SonarRuleImpl;
import com.aquarellian.genar.data.entity.interfaces.SonarRule;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 17:13
 * <p/>
 * $Id$
 */
public class SonarRuleCreator implements InstanceCreator<SonarRule> {

    public SonarRule createInstance(Type type) {
        return new SonarRuleImpl();
    }
}
