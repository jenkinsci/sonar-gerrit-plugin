package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.entity.SonarComponentImpl;
import com.aquarellian.genar.data.entity.interfaces.SonarComponent;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 17:12
 * <p/>
 * $Id$
 */
public class SonarComponentCreator implements InstanceCreator<SonarComponent> {

    public SonarComponent createInstance(Type type) {
        return new SonarComponentImpl();
    }
}
