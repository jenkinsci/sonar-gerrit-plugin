package org.jenkinsci.plugins.sonargerrit.sonar;

/** @author Réda Housni Alaoui */
interface SonarQubeIssueDecorator {

  SimpleIssue decorate(SimpleIssue issue);

  class Noop implements SonarQubeIssueDecorator {

    public static final SonarQubeIssueDecorator INSTANCE = new Noop();

    private Noop() {}

    @Override
    public SimpleIssue decorate(SimpleIssue issue) {
      return issue;
    }
  }
}
