package org.jenkinsci.plugins.sonargerrit.test_infrastructure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Réda Housni Alaoui
 */
public class ObjectMappers {

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

  public static ObjectMapper get() {
    return OBJECT_MAPPER;
  }
}
