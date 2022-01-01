package org.jenkinsci.plugins.sonargerrit.test_infrastructure;

/** @author Réda Housni Alaoui */
public interface CloseableResource<T> {

  T resource();

  void close();
}
