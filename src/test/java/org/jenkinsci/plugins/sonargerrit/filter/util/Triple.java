package org.jenkinsci.plugins.sonargerrit.filter.util;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 20:49 $Id$ */
public class Triple<A, B, C> extends Pair<A, B> {
  private C c;

  public Triple(A a, B b, C c) {
    super(a, b);
    this.c = c;
  }

  public C getThird() {
    return c;
  }
}
