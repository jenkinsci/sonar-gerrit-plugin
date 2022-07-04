package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.Predicate;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

/** @author Réda Housni Alaoui */
public class ByGlobPatternPredicate implements Predicate<Issue> {

  private final PathMatcher pathMatcher;
  private final boolean negated;

  public ByGlobPatternPredicate(String pattern) {
    this(pattern, false);
  }

  public ByGlobPatternPredicate(String pattern, boolean negated) {
    this(FileSystems.getDefault().getPathMatcher("glob:" + pattern), negated);
  }

  public ByGlobPatternPredicate(PathMatcher pathMatcher, boolean negated) {
    this.pathMatcher = pathMatcher;
    this.negated = negated;
  }

  public ByGlobPatternPredicate negate() {
    return new ByGlobPatternPredicate(pathMatcher, !negated);
  }

  @Override
  public boolean apply(Issue input) {
    String filepath = StringUtils.prependIfMissing(input.getFilepath(), "/");
    boolean matched = pathMatcher.matches(Paths.get(filepath));
    if (negated) {
      return !matched;
    } else {
      return matched;
    }
  }
}
