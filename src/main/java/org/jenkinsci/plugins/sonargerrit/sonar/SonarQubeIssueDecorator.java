package org.jenkinsci.plugins.sonargerrit.sonar;

/** @author Réda Housni Alaoui */
interface SonarQubeIssueDecorator {

  SonarQubeIssue decorate(SonarQubeIssue issue);

  class Noop implements SonarQubeIssueDecorator {

    public static final SonarQubeIssueDecorator INSTANCE = new Noop();

    private Noop() {}

    @Override
    public SonarQubeIssue decorate(SonarQubeIssue issue) {
      return issue;
    }
  }
}
