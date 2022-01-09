package org.jenkinsci.plugins.sonargerrit.review;

import java.util.HashSet;
import java.util.Set;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** @author Réda Housni Alaoui */
@Restricted(NoExternalUse.class)
public class DiffInfos {

  private DiffInfos() {}

  public static Set<Integer> toChangedLines(DiffInfo diffInfo) {
    Set<Integer> rangeSet = new HashSet<>();
    int processed = 0;
    for (DiffInfo.ContentEntry contentEntry : diffInfo.content) {
      if (contentEntry.ab != null) {
        processed += contentEntry.ab.size();
      } else if (contentEntry.b != null) {
        int start = processed + 1;
        int end = processed + contentEntry.b.size();
        for (int i = start; i <= end; i++) {
          rangeSet.add(i);
        }
        processed = end;
      }
    }
    return rangeSet;
  }
}
