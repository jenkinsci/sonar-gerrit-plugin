package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.FileInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.junit.jupiter.api.Test;

class GerritRevisionTest {

  @Test
  void fetchesDiffOnlyForRequestedChangedFiles() throws RestApiException {
    List<String> diffedPaths = new ArrayList<>();
    GerritRevision revision =
        GerritRevision.load(
            new DummyRevisionApi(Map.of("a.java", List.of(1, 2), "b.java", List.of(1, 2))) {
              @Override
              public Map<String, FileInfo> files() {
                return Map.of(
                    "a.java", new FileInfo(), "b.java", new FileInfo(), "c.java", new FileInfo());
              }

              @Override
              protected DiffInfo generateDiffInfoByPath(String path) {
                diffedPaths.add(path);
                return super.generateDiffInfoByPath(path);
              }
            });

    Map<String, Set<Integer>> fileToChangedLines =
        revision.fetchFileToChangedLines(Set.of("a.java", "unchanged.java"));

    assertThat(diffedPaths).containsExactly("a.java");
    assertThat(fileToChangedLines).containsOnlyKeys("a.java");
    assertThat(fileToChangedLines.get("a.java")).containsExactlyInAnyOrder(2, 3);
  }
}
