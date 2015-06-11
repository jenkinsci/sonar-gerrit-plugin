package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.entity.SonarIssueImpl;
import com.aquarellian.genar.data.entity.interfaces.SonarIssue;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 17:08
 * <p/>
 * $Id$
 */
public class SonarIssueCreator implements InstanceCreator<SonarIssue> {
    public SonarIssue createInstance(Type type) {
        return new SonarIssueImpl();
    }
}
