package org.jenkinsci.plugins.sonargerrit.review;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.google.common.annotations.VisibleForTesting;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 16:26
 *
 * <p>$Id$
 */
public class GerritRevisionWrapper implements RevisionAdapter {
  private final RevisionApi revision;
  protected Set<String> changedFiles;
  protected boolean dataLoaded;
  private Map<String, Set<Integer>> file2changedLines;

  public GerritRevisionWrapper(RevisionApi revision) {
    this.revision = revision;
    dataLoaded = false;
  }

  public void loadData() throws RestApiException {
    this.dataLoaded = true;
    this.changedFiles = calculateChangedFiles();
    this.file2changedLines = calculateFile2ChangedLines();
  }

  public void sendReview(ReviewInput reviewInput) throws RestApiException {
    revision.review(reviewInput);
  }

  @Override
  public Set<String> getChangedFiles() {
    if (!dataLoaded) {
      throw new IllegalStateException(
          getLocalized("jenkins.plugin.error.gerrit.revision.data.not.loaded"));
    }
    return changedFiles;
  }

  @Override
  public Map<String, Set<Integer>> getFileToChangedLines() {
    if (!dataLoaded) {
      throw new IllegalStateException(
          getLocalized("jenkins.plugin.error.gerrit.revision.data.not.loaded"));
    }
    return file2changedLines;
  }

  protected Set<String> calculateChangedFiles() throws RestApiException {
    return revision.files().keySet();
  }

  protected Map<String, Set<Integer>> calculateFile2ChangedLines() throws RestApiException {
    Map<String, Set<Integer>> file2changedLinesInfo = new HashMap<>();
    Map<String, FileInfo> files = revision.files();
    for (String filename : files.keySet()) {
      Set<Integer> changedLinesByFile = getChangedLinesByFile(filename);
      file2changedLinesInfo.put(filename, changedLinesByFile);
    }
    return file2changedLinesInfo;
  }

  protected Set<Integer> getChangedLinesByFile(String filename) throws RestApiException {
    DiffInfo diffInfo = revision.file(filename).diff();
    return getChangedLines(diffInfo);
  }

  @VisibleForTesting
  Set<Integer> getChangedLines(DiffInfo diffInfo) {
    Set<Integer> rangeSet = new HashSet<>();
    int processed = 0;
    for (DiffInfo.ContentEntry contentEntry : diffInfo.content) {
      if (contentEntry.ab != null) {
        processed += contentEntry.ab.size();
      } else if (contentEntry.b != null) {
        int start = processed + 1;
        int end = processed + contentEntry.b.size();
        for (int i = start; i <= end; i++) { // todo use guava Range for this purpose?
          rangeSet.add(i);
        }
        processed = end;
      }
    }
    return rangeSet;
  }
}
