package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.FileInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GerritRevisionTest {

  @Test
  @DisplayName("Fetches the diff only of the changed files carrying a Sonar issue")
  void test1() throws RestApiException {
    List<String> diffedPaths = new ArrayList<>();
    GerritRevision revision =
        GerritRevision.load(
            new DummyRevisionApi(
                Map.of("withIssue.java", List.of(1, 2), "withoutIssue.java", List.of(1, 2))) {
              @Override
              public Map<String, FileInfo> files() {
                return Map.of(
                    "withIssue.java", new FileInfo(), "withoutIssue.java", new FileInfo());
              }

              @Override
              protected DiffInfo generateDiffInfoByPath(String path) {
                diffedPaths.add(path);
                return super.generateDiffInfoByPath(path);
              }
            });

    Map<String, Set<Integer>> fileToChangedLines =
        revision.fetchFileToChangedLines(Set.of("withIssue.java", "outOfChangeWithIssue.java"));

    assertThat(diffedPaths).containsExactly("withIssue.java");
    assertThat(fileToChangedLines).containsOnlyKeys("withIssue.java");
    assertThat(fileToChangedLines.get("withIssue.java")).containsExactlyInAnyOrder(2, 3);
  }
}
