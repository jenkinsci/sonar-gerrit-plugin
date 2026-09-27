package org.jenkinsci.plugins.sonargerrit.gerrit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RevisionApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 16:26
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public class GerritRevision implements Revision {

  private final RevisionApi revision;

  private final Set<String> changedFiles;

  private GerritRevision(RevisionApi revision, Set<String> changedFiles) {
    this.revision = revision;
    this.changedFiles = changedFiles;
  }

  public static GerritRevision load(RevisionApi revision) throws RestApiException {
    return new GerritRevision(revision, revision.files().keySet());
  }

  public void sendReview(ReviewInput reviewInput) throws RestApiException {
    revision.review(reviewInput);
  }

  @Override
  public Set<String> getChangedFiles() {
    return changedFiles;
  }

  /**
   * Fetching a diff costs one Gerrit request per file, so only the given files are fetched. Files
   * absent from the revision are skipped.
   */
  public Map<String, Set<Integer>> fetchFileToChangedLines(Set<String> filenames)
      throws RestApiException {
    Map<String, Set<Integer>> fileToChangedLines = new HashMap<>();
    for (String filename : filenames) {
      if (!changedFiles.contains(filename)) {
        continue;
      }
      DiffInfo diffInfo = revision.file(filename).diff();
      fileToChangedLines.put(filename, DiffInfos.toChangedLines(diffInfo));
    }
    return fileToChangedLines;
  }
}
