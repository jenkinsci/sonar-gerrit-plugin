package org.jenkinsci.plugins.sonargerrit.gerrit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RevisionApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.FileInfo;
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
  private final Map<String, Set<Integer>> file2changedLines;

  private GerritRevision(
      RevisionApi revision, Set<String> changedFiles, Map<String, Set<Integer>> file2changedLines) {
    this.revision = revision;
    this.changedFiles = changedFiles;
    this.file2changedLines = file2changedLines;
  }

  public static GerritRevision load(RevisionApi revision) throws RestApiException {
    return new Loader(revision).load();
  }

  public void sendReview(ReviewInput reviewInput) throws RestApiException {
    revision.review(reviewInput);
  }

  @Override
  public Set<String> getChangedFiles() {
    return changedFiles;
  }

  public Map<String, Set<Integer>> getFileToChangedLines() {
    return file2changedLines;
  }

  private static class Loader {
    private final RevisionApi revision;

    Loader(RevisionApi revision) {
      this.revision = revision;
    }

    public GerritRevision load() throws RestApiException {
      return new GerritRevision(revision, calculateChangedFiles(), calculateFile2ChangedLines());
    }

    private Set<String> calculateChangedFiles() throws RestApiException {
      return revision.files().keySet();
    }

    private Map<String, Set<Integer>> calculateFile2ChangedLines() throws RestApiException {
      Map<String, Set<Integer>> file2changedLinesInfo = new HashMap<>();
      Map<String, FileInfo> files = revision.files();
      for (String filename : files.keySet()) {
        Set<Integer> changedLinesByFile = getChangedLinesByFile(filename);
        file2changedLinesInfo.put(filename, changedLinesByFile);
      }
      return file2changedLinesInfo;
    }

    private Set<Integer> getChangedLinesByFile(String filename) throws RestApiException {
      DiffInfo diffInfo = revision.file(filename).diff();
      return DiffInfos.toChangedLines(diffInfo);
    }
  }
}
