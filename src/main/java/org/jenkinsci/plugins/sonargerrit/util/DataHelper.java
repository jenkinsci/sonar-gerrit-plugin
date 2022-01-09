package org.jenkinsci.plugins.sonargerrit.util;

import hudson.Util;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.11.2017 22:11
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public final class DataHelper {
  private DataHelper() {}

  public static <T extends Enum<T>> String checkEnumValueCorrect(Class<T> clazz, String value) {
    if (Util.fixEmptyAndTrim(value) == null) {
      return null;
    }
    try {
      Enum.valueOf(clazz, value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
    return value;
  }

  public static Integer parseNumber(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static void checkNotEmpty(String value, String errMessage) {
    if (Util.fixEmpty(value) == null) {
      throw new IllegalArgumentException(errMessage);
    }
  }

  public static void checkNotEmpty(Object value, String errMessage) {
    if (value == null) {
      throw new IllegalArgumentException(errMessage);
    }
  }

  public static void checkNotNull(Object value, String errMessage) {
    if (value == null) {
      throw new IllegalStateException(errMessage);
    }
  }
}
