package org.jenkinsci.plugins.sonargerrit.filter.util;

import com.google.gerrit.extensions.common.DiffInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 15.11.2017 22:07 $Id$ */
public class DummyRevision {
  public List<FileDiff> diffs;

  public Map<String, DiffInfo> toMap() {
    Map<String, DiffInfo> res = new HashMap<>();
    for (FileDiff diff : diffs) {
      res.put(diff.filename, diff.diffInfo);
    }
    return res;
  }
}
