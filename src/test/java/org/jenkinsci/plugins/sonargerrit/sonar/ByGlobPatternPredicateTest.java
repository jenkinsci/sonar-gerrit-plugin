package org.jenkinsci.plugins.sonargerrit.sonar;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** @author Réda Housni Alaoui */
class ByGlobPatternPredicateTest {

  @Test
  void test() {
    assertThat(new ByGlobPatternPredicate("**/*").apply(new DummyIssue("/foo.bar"))).isTrue();
    assertThat(new ByGlobPatternPredicate("/foo.bar").apply(new DummyIssue("/foo.bar"))).isTrue();
    assertThat(new ByGlobPatternPredicate("/foo/*").apply(new DummyIssue("/foo/bar"))).isTrue();
    assertThat(new ByGlobPatternPredicate("/foo/**").apply(new DummyIssue("/foo/bar/baz")))
        .isTrue();
    assertThat(new ByGlobPatternPredicate("/foo/**").apply(new DummyIssue("/foo/bar/baz/bat.toto")))
        .isTrue();
    assertThat(new ByGlobPatternPredicate("/bar/**").apply(new DummyIssue("/foo/bar/baz/bat.toto")))
        .isFalse();
  }

  private static class DummyIssue implements Issue {

    private final String filePath;

    DummyIssue(String filePath) {
      this.filePath = requireNonNull(filePath);
    }

    @Override
    public String inspectorName() {
      return null;
    }

    @Override
    public String inspectionId() {
      return null;
    }

    @Override
    public Optional<String> detailUrl() {
      return Optional.empty();
    }

    @Override
    public String getFilepath() {
      return filePath;
    }

    @Override
    public String getKey() {
      return null;
    }

    @Override
    public String getComponent() {
      return null;
    }

    @Override
    public Integer getLine() {
      return null;
    }

    @Override
    public String getMessage() {
      return null;
    }

    @Override
    public Severity getSeverity() {
      return null;
    }

    @Override
    public String getRule() {
      return null;
    }

    @Override
    public String getRuleUrl() {
      return null;
    }

    @Override
    public String getStatus() {
      return null;
    }

    @Override
    public boolean isNew() {
      return false;
    }

    @Override
    public Date getCreationDate() {
      return null;
    }
  }
}
