package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.Predicate;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

/** @author Réda Housni Alaoui */
public class ByGlobPatternPredicate implements Predicate<Issue> {

  private final PathMatcher pathMatcher;

  public ByGlobPatternPredicate(String pattern) {
    this.pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
  }

  @Override
  public boolean apply(Issue input) {
    String filepath = StringUtils.prependIfMissing(input.getFilepath(), "/");
    return pathMatcher.matches(Paths.get(filepath));
  }
}
