package org.jenkinsci.plugins.sonargerrit.signature;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.beanutils.PropertyUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.Assert;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 17.11.2017 13:20 $Id$ */

/*
 * Reflection methods for tests to be triggered on plugin signature changes
 * */

public abstract class ConfigurationUpdateTest {

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

  protected void invokeSetter(SonarToGerritPublisher obj, String field, Object value)
      throws ReflectiveOperationException {
    this.invokeSetter(obj, field, value, false);
  }

  protected void invokeSetter(Object obj, String field, Object value, boolean deprecated)
      throws ReflectiveOperationException {
    PropertyDescriptor e1 = PropertyUtils.getPropertyDescriptor(obj, field);
    Assert.assertNotNull(
        String.format("There is no public setter for field %s", field),
        e1); // check setter method exists
    Method wm1 = e1.getWriteMethod();
    Assert.assertNotNull(
        String.format("There is no annotation @DataBoundSetter for setter for field %s", field),
        wm1.getAnnotation(DataBoundSetter.class)); // check setter method annotated
    if (deprecated) {
      Assert.assertNotNull(
          String.format("Setter for field %s should be marked as @deprecated", field),
          wm1.getAnnotation(Deprecated.class)); // check setter method deprecated
    }
    wm1.setAccessible(true);
    wm1.invoke(obj, value);
  }

  protected void invokeSetter(
      SonarToGerritPublisher obj, Object value, boolean deprecated, String... fields)
      throws ReflectiveOperationException {
    if (fields.length == 1) {
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

  protected Object invokeConstructor(String className, String[] paramClasses, Object[] params)
      throws ReflectiveOperationException {
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
    // Assert.assertNotNull(c.getAnnotation(DataBoundConstructor.class));
    return c.newInstance(params);
  }

  protected Object invokeMethod(
      Object obj, String methodName, Class<? extends Annotation>... annotations)
      throws ReflectiveOperationException {
    Class<?> aClass = obj.getClass();
    Method declaredMethod = aClass.getDeclaredMethod(methodName);
    for (Class<? extends Annotation> a : annotations) {
      Assert.assertTrue(declaredMethod.isAnnotationPresent(a));
    }
    return declaredMethod.invoke(obj);
  }

  protected Object invokeMethod(
      Object obj, String methodName, Object parameter, Class<? extends Annotation>... annotations)
      throws ReflectiveOperationException {
    Method declaredMethod = getDeclaredMethod(obj.getClass(), methodName, parameter.getClass());
    for (Class<? extends Annotation> a : annotations) {
      Assert.assertTrue(declaredMethod.isAnnotationPresent(a));
    }
    return declaredMethod.invoke(obj, parameter);
  }

  private Method getDeclaredMethod(Class<?> aClass, String methodName, Class<?> paramClass) {
    Method declaredMethod = tryGetDeclaredMethod(aClass, methodName, paramClass);
    if (declaredMethod == null) {
      Class<?> superclass = paramClass.getSuperclass();
      if (superclass != null) {
        declaredMethod = getDeclaredMethod(aClass, methodName, superclass);
      }
      if (declaredMethod == null) {
        for (Class<?> anInterface : paramClass.getInterfaces()) {
          declaredMethod = getDeclaredMethod(aClass, methodName, anInterface);
          if (declaredMethod != null) {
            return declaredMethod;
          }
        }
      }
    }
    return declaredMethod;
  }

  private Method tryGetDeclaredMethod(Class<?> aClass, String methodName, Class<?> paramClass) {
    try {
      return aClass.getDeclaredMethod(methodName, paramClass);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
