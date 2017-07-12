package org.jenkinsci.plugins.sonargerrit.signature;

import org.apache.commons.beanutils.PropertyUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 17.11.2017 13:20
 * $Id$
 */

/*
* Reflection methods for tests to be triggered on plugin signature changes
* */

public class ConfigurationUpdateTest {

    protected Object invokeGetter(Object obj, String... field) throws ReflectiveOperationException {
        Object res = null;
        Object object = obj;
        for (String f : field) {
            Field wm = null;
            Class<?> aClass = object.getClass();
            do {
                try {
                    wm = aClass.getDeclaredField(f);
                } catch (NoSuchFieldException e) {
                    Class<?> superclass = aClass.getSuperclass();
                    if (superclass != null && superclass.getPackage().toString().contains("sonargerrit")) {
                        aClass = superclass;
                    } else {
                        throw e;
                    }
                }
            } while (wm == null);
            wm.setAccessible(true);
            res = wm.get(object);
            if (res instanceof Collection) {
                res = ((Collection<?>) res).toArray()[0];
            }
            object = res;
        }
        return res;
    }

    protected void invokeSetter(SonarToGerritPublisher obj, String field, Object value) throws ReflectiveOperationException {
        this.invokeSetter(obj, field, value, false);
    }

    protected void invokeSetter(Object obj, String field, Object value, boolean deprecated) throws ReflectiveOperationException {
        PropertyDescriptor e1 = PropertyUtils.getPropertyDescriptor(obj, field);
        Assert.assertNotNull(String.format("There is no public setter for field %s", field), e1); // check setter method exists
        Method wm1 = e1.getWriteMethod();
        Assert.assertNotNull(String.format("There is no annotation @DataBoundSetter for setter for field %s", field),
                wm1.getAnnotation(DataBoundSetter.class));  // check setter method annotated
        if (deprecated) {
            Assert.assertNotNull(String.format("Setter for field %s should be marked as @deprecated", field),
                    wm1.getAnnotation(Deprecated.class));  // check setter method deprecated
        }
        wm1.setAccessible(true);
        wm1.invoke(obj, value);
    }

    protected void invokeSetter(SonarToGerritPublisher obj, Object value, boolean deprecated, String... fields) throws ReflectiveOperationException {
        if (fields.length == 1){
            invokeSetter(obj, fields[0], value, deprecated);
            return;
        }
        Object object = invokeGetter(obj, Arrays.copyOf(fields, fields.length - 1));
        invokeSetter(object, fields[fields.length - 1], value, deprecated);
    }

    protected SonarToGerritPublisher invokeConstructor() throws ReflectiveOperationException {
        Constructor<SonarToGerritPublisher> c = SonarToGerritPublisher.class.getConstructor();
        Assert.assertNotNull(c.getAnnotation(DataBoundConstructor.class));
        return c.newInstance();
    }

    protected Object invokeConstructor(String className, String[] paramClasses, Object[] params) throws ReflectiveOperationException {
        Class<?>[] classes = new Class[paramClasses.length];
        for (int i = 0; i < paramClasses.length; i++) {
            String paramClass = paramClasses[i];
            if (paramClass.contains(".")) {
                classes[i] = Class.forName(paramClass);
            } else if ("boolean".equals(paramClass)) {
                classes[i] = Boolean.TYPE;
            } else if ("integer".equals(paramClass)) {
                classes[i] = Integer.TYPE;
            }
        }

        Constructor<?> c = Class.forName(className).getConstructor(classes);
        //Assert.assertNotNull(c.getAnnotation(DataBoundConstructor.class));
        return c.newInstance(params);
    }
}
