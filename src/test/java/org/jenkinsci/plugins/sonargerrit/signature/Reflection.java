package org.jenkinsci.plugins.sonargerrit.signature;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.beanutils.PropertyUtils;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.jupiter.api.Assertions;
import org.kohsuke.stapler.DataBoundSetter;

/** @author Réda Housni Alaoui */
class Reflection {

  public static Object invokeGetter(Object obj, String... fields)
      throws ReflectiveOperationException {
    Object currentObj = obj;
    for (String field : fields) {
      if (currentObj instanceof Collection<?>) {
        currentObj =
            ((Collection<?>) currentObj).stream().findFirst().orElseThrow(RuntimeException::new);
      }
      currentObj = PropertyUtils.getProperty(currentObj, field);
    }
    return currentObj;
  }

  public static void invokeSetter(SonarToGerritPublisher obj, String field, Object value)
      throws ReflectiveOperationException {
    invokeSetter(obj, field, value, false);
  }

  public static void invokeSetter(Object obj, String field, Object value, boolean deprecated)
      throws ReflectiveOperationException {
    PropertyDescriptor e1 = PropertyUtils.getPropertyDescriptor(obj, field);
    Assertions.assertNotNull(
        e1,
        String.format(
            "There is no public setter for field %s", field)); // check setter method exists
    Method wm1 = e1.getWriteMethod();
    Assertions.assertNotNull(
        wm1.getAnnotation(DataBoundSetter.class),
        String.format(
            "There is no annotation @DataBoundSetter for setter for field %s",
            field)); // check setter method annotated
    if (deprecated) {
      Assertions.assertNotNull(
          wm1.getAnnotation(Deprecated.class),
          String.format(
              "Setter for field %s should be marked as @deprecated",
              field)); // check setter method deprecated
    }
    wm1.setAccessible(true);
    wm1.invoke(obj, value);
  }

  public static void invokeSetter(
      SonarToGerritPublisher obj, Object value, boolean deprecated, String... fields)
      throws ReflectiveOperationException {
    if (fields.length == 1) {
      invokeSetter(obj, fields[0], value, deprecated);
      return;
    }
    Object object = invokeGetter(obj, Arrays.copyOf(fields, fields.length - 1));
    invokeSetter(object, fields[fields.length - 1], value, deprecated);
  }

  public static Object invokeConstructor(String className, String[] paramClasses, Object[] params)
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
    return c.newInstance(params);
  }

  public static Object invokeMethod(
      Object obj, String methodName, Object parameter, Class<? extends Annotation>... annotations)
      throws ReflectiveOperationException {
    Method declaredMethod = getDeclaredMethod(obj.getClass(), methodName, parameter.getClass());
    for (Class<? extends Annotation> a : annotations) {
      Assertions.assertTrue(declaredMethod.isAnnotationPresent(a));
    }
    return declaredMethod.invoke(obj, parameter);
  }

  private static Method getDeclaredMethod(Class<?> aClass, String methodName, Class<?> paramClass) {
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

  private static Method tryGetDeclaredMethod(
      Class<?> aClass, String methodName, Class<?> paramClass) {
    try {
      return aClass.getDeclaredMethod(methodName, paramClass);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
