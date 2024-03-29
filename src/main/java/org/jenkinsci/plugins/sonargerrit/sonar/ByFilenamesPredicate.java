package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.base.Predicate;
import java.util.HashSet;
import java.util.Set;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 17:43
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public class ByFilenamesPredicate implements Predicate<Issue> {
  private final Set<String> allowedComponents;

  private ByFilenamesPredicate(Set<String> allowedComponents) {
    this.allowedComponents = new HashSet<>();
    if (allowedComponents != null) {
      this.allowedComponents.addAll(allowedComponents);
    }
  }

  @Override
  public boolean apply(Issue issue) {
    return allowedComponents != null && allowedComponents.contains(issue.getFilepath());
  }

  public static ByFilenamesPredicate apply(Set<String> allowedComponents) {
    return new ByFilenamesPredicate(allowedComponents);
  }
}
