package org.jenkinsci.plugins.sonargerrit.gerrit;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 16:59
 *
 * <p>$Id$
 */
public class Pair<A, B> {
  private final A a;
  private final B b;

  public Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }

  public A getFirst() {
    return a;
  }

  public B getSecond() {
    return b;
  }
}
