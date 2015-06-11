package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.entity.SonarUserImpl;
import com.aquarellian.genar.data.entity.interfaces.SonarUser;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 17:11
 * <p/>
 * $Id$
 */
public class SonarUserCreator implements InstanceCreator<SonarUser> {

    public SonarUser createInstance(Type type) {
        return new SonarUserImpl();
    }
}
