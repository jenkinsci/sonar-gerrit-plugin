package org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefSpec;

/** @author Réda Housni Alaoui */
public class GerritGit {
  private static final String CHANGE_ID = "Change-Id: I";

  private final GerritServer gerrit;
  private final Git git;
  private final String projectName;
  private final String httpUrl;
  private final PersonIdent agent;

  public static GerritGit createAndCloneRepository(GerritServer gerrit, Path workspace)
      throws GitAPIException {
    String projectName = gerrit.createProject();
    Git git =
        Git.cloneRepository()
            .setCredentialsProvider(gerrit.gitAdminCredentialsProvider())
            .setURI(gerrit.getGitRepositoryHttpUrl(projectName))
            .setDirectory(workspace.toFile())
            .call();
    return new GerritGit(gerrit, git, projectName);
  }

  private GerritGit(GerritServer gerrit, Git git, String projectName) {
    this.gerrit = gerrit;
    this.git = git;
    this.projectName = projectName;
    httpUrl = gerrit.getGitRepositoryHttpUrl(projectName);
    this.agent = new PersonIdent("Administrator", gerrit.adminEmailAddress());
  }

  public String httpUrl() {
    return httpUrl;
  }

  public Path workTree() {
    return git.getRepository().getWorkTree().toPath();
  }

  public CommitCommand commit(String message, boolean amend) throws IOException {
    String changeIdLine = null;
    if (amend) {
      Repository repository = git.getRepository();
      String previousMessage =
          repository.parseCommit(repository.resolve(Constants.HEAD)).getFullMessage();
      changeIdLine =
          Arrays.stream(previousMessage.split("\n"))
              .filter(msg -> msg.startsWith(CHANGE_ID))
              .findFirst()
              .orElse(null);
    }

    if (changeIdLine == null) {
      changeIdLine =
          CHANGE_ID + DigestUtils.sha1Hex(String.format("%s|%s", UUID.randomUUID(), message));
    }

    return git.commit()
        .setAmend(amend)
        .setMessage(message + "\n\n" + changeIdLine)
        .setAuthor(agent)
        .setCommitter(agent);
  }

  public GerritGit push() throws GitAPIException {
    git.push().setCredentialsProvider(gerrit.gitAdminCredentialsProvider()).call();
    return this;
  }

  /** @return The gerrit change branch ref name */
  public GerritChange createGerritChangeForMaster()
      throws GitAPIException, RestApiException, UnsupportedEncodingException {
    return createGerritChange("master");
  }

  public GerritChange createGerritChange(String targetBranchName)
      throws GitAPIException, RestApiException, UnsupportedEncodingException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    git.push()
        .setCredentialsProvider(gerrit.gitAdminCredentialsProvider())
        .setRemote("origin")
        .setRefSpecs(new RefSpec("HEAD:refs/for/" + targetBranchName))
        .setOutputStream(outputStream)
        .call();
    String output = outputStream.toString(StandardCharsets.UTF_8.name());
    Matcher matcher = Pattern.compile("/c/" + projectName + "/\\+/(\\d+)").matcher(output);
    if (!matcher.find()) {
      throw new IllegalStateException("Could not parse change numeric id");
    }
    String changeNumericId = matcher.group(1);
    return new GerritChange(gerrit.api().changes().id(changeNumericId));
  }

  public GerritGit resetToOriginMaster() throws GitAPIException {
    git.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/master").call();
    return this;
  }

  public GerritGit addAndCommitFile(String relativePath, String content)
      throws IOException, GitAPIException {
    return addAndCommitFile(relativePath, content, false);
  }

  public GerritGit addAndCommitFile(String relativePath, String content, boolean amend)
      throws IOException, GitAPIException {
    addFile(relativePath, content);
    commit("Add " + relativePath, amend).call();
    return this;
  }

  public GerritGit addFile(String relativePath, String content)
      throws IOException, GitAPIException {
    Path filePath = workTree().resolve(relativePath);
    Files.createDirectories(filePath.getParent());
    Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
    git.add().addFilepattern(relativePath).call();
    return this;
  }
}
