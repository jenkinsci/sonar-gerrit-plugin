package org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;

/** @author Réda Housni Alaoui */
public class GerritGit {

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

  public String projectName() {
    return projectName;
  }

  public Path workTree() {
    return git.getRepository().getWorkTree().toPath();
  }

  public AddCommand add() {
    return git.add();
  }

  public RmCommand rm() {
    return git.rm();
  }

  public CommitCommand commit(String message) {
    String changeId = "I" + DigestUtils.sha1Hex(String.format("%s|%s", UUID.randomUUID(), message));
    return git.commit()
        .setMessage(message + "\n\nChange-Id: " + changeId)
        .setAuthor(agent)
        .setCommitter(agent);
  }

  public GerritGit push() throws GitAPIException {
    git.push().setCredentialsProvider(gerrit.gitAdminCredentialsProvider()).call();
    return this;
  }

  public GerritGit pushTags() throws GitAPIException {
    git.push().setPushTags().setCredentialsProvider(gerrit.gitAdminCredentialsProvider()).call();
    return this;
  }

  public GerritGit pull() throws GitAPIException {
    git.pull().setCredentialsProvider(gerrit.gitAdminCredentialsProvider()).call();
    return this;
  }

  public List<Ref> listTags() throws GitAPIException {
    return git.tagList().call();
  }

  public GerritGit createGitTag(String name) throws GitAPIException {
    git.tag().setTagger(agent).setName(name).call();
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

  public GerritGit checkoutMaster() throws GitAPIException {
    git.checkout().setName("master").call();
    return this;
  }

  public GerritGit fetchOrigin() throws GitAPIException {
    git.fetch().setRemote("origin").call();
    return this;
  }

  public GerritGit resetToOriginMaster() throws GitAPIException {
    git.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/master").call();
    return this;
  }

  public GerritGit addAndCommitGroovyFile(String relativePath, String content)
      throws IOException, GitAPIException {
    return addAndCommitFile(relativePath, content);
  }

  public GerritGit addAndCommitFile(String relativePath, String content)
      throws IOException, GitAPIException {
    addFile(relativePath, content);
    commit("Add " + relativePath).call();
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

  public GerritGit checkoutAndPushNewBranch(String name) throws GitAPIException {
    git.checkout()
        .setName(name)
        .setCreateBranch(true)
        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
        .call();
    git.push().setCredentialsProvider(gerrit.gitAdminCredentialsProvider()).call();
    return this;
  }
}
