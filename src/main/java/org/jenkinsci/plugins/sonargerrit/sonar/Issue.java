package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.util.Date;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 03.12.2017 14:00 $Id$ */
@Restricted(NoExternalUse.class)
public interface Issue {
  String getFilepath();

  String getKey();

  String getComponent();

  Integer getLine();

  String getMessage();

  Severity getSeverity();

  String getRule();

  String getRuleLink();

  String getStatus();

  boolean isNew();

  Date getCreationDate();

  static Multimap<String, Issue> asMultimap(Iterable<Issue> issues) {
    final Multimap<String, Issue> multimap = LinkedListMultimap.create();
    for (Issue issue : issues) {
      multimap.put(issue.getFilepath(), issue);
    }
    return multimap;
  }
}
