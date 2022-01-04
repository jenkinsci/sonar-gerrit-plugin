package org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.redaalaoui.gerrit_rest_java_client.rest.GerritAuthData;
import me.redaalaoui.gerrit_rest_java_client.rest.GerritRestApiFactory;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.GerritApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.access.AccessSectionInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.access.PermissionInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.access.PermissionRuleInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.access.ProjectAccessInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.projects.ProjectApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.LabelDefinitionInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.CloseableResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/** @author Réda Housni Alaoui */
public class GerritServer {

  private static final Logger LOG = LoggerFactory.getLogger(GerritServer.class);

  private static final int HTTP_PORT = 8080;
  private static final int SSH_PORT = 29418;

  private static final String NETWORK_ALIAS = "gerrit";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "secret";
  public static final String CODE_QUALITY_LABEL = "Code-Quality";

  private final GenericContainer<?> container;
  private final String url;
  private final GerritApi gerritApi;

  public static CloseableResource<GerritServer> start(Network network) {
    GerritServer gerritServer = new GerritServer(network);
    return new CloseableResource<GerritServer>() {
      @Override
      public GerritServer resource() {
        return gerritServer;
      }

      @Override
      public void close() {
        gerritServer.stop();
      }
    };
  }

  private GerritServer(Network network) {
    container =
        new GenericContainer<>("gerritcodereview/gerrit:3.4.1-ubuntu20")
            .withLogConsumer(new Slf4jLogConsumer(LOG))
            .withExposedPorts(HTTP_PORT, SSH_PORT)
            .withNetwork(network)
            .withNetworkAliases(NETWORK_ALIAS)
            .withStartupTimeout(Duration.of(30, ChronoUnit.MINUTES));
    container.start();

    url = "http://localhost:" + container.getMappedPort(HTTP_PORT);

    gerritApi =
        new GerritRestApiFactory().create(new GerritAuthData.Basic(url, USERNAME, PASSWORD));

    try {
      addLabels(gerritApi);
    } catch (RestApiException e) {
      throw new RuntimeException(e);
    }
  }

  private static void addLabels(GerritApi gerritApi) throws RestApiException {
    ProjectApi allProjects = gerritApi.projects().name("All-Projects");

    LabelDefinitionInput labelDefinition = new LabelDefinitionInput();
    labelDefinition.function = "MaxWithBlock";

    Map<String, String> values = new HashMap<>();
    values.put("-1", "Fails");
    values.put("0", "No score");
    values.put("+1", "Verified");
    labelDefinition.values = values;
    labelDefinition.copyAllScoresIfNoChange = true;
    labelDefinition.defaultValue = 0;

    allProjects.label("Verified").create(labelDefinition);
    allProjects.label(CODE_QUALITY_LABEL).create(labelDefinition);

    String adminGroupId = gerritApi.groups().id("1").get().id;

    AccessSectionInfo accessSection = new AccessSectionInfo();
    PermissionRuleInfo permissionRule =
        new PermissionRuleInfo(PermissionRuleInfo.Action.ALLOW, false);
    permissionRule.min = -1;
    permissionRule.max = 1;
    Map<String, PermissionRuleInfo> rules = Collections.singletonMap(adminGroupId, permissionRule);

    accessSection.permissions = new HashMap<>();

    PermissionInfo verifiedPermission = new PermissionInfo("Verified", false);
    verifiedPermission.rules = rules;
    accessSection.permissions.put("label-Verified", verifiedPermission);

    PermissionInfo codeQualityPermission = new PermissionInfo(CODE_QUALITY_LABEL, false);
    codeQualityPermission.rules = rules;
    accessSection.permissions.put("label-" + CODE_QUALITY_LABEL, codeQualityPermission);

    ProjectAccessInput access = new ProjectAccessInput();
    access.add = Collections.singletonMap("refs/heads/*", accessSection);
    allProjects.access(access);
  }

  public String dockerHostname() {
    return NETWORK_ALIAS;
  }

  public String dockerUrl() {
    return "http://" + NETWORK_ALIAS + ":" + HTTP_PORT;
  }

  public String url() {
    return "http://localhost:" + container.getMappedPort(HTTP_PORT);
  }

  public String getGitRepositoryHttpUrl(String projectName) {
    return "http://localhost:" + container.getMappedPort(HTTP_PORT) + "/" + projectName;
  }

  public String getGitRepositoryHttpDockerUrl(String projectName) {
    return "http://" + NETWORK_ALIAS + ":" + HTTP_PORT + "/" + projectName;
  }

  public String adminUsername() {
    return USERNAME;
  }

  public String adminEmailAddress() {
    return "admin@example.com";
  }

  public String adminPassword() {
    return PASSWORD;
  }

  public CredentialsProvider gitAdminCredentialsProvider() {
    return new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD);
  }

  public void addSshPublicKeyToAdminUser(String key) {
    try {
      gerritApi.accounts().self().addSshKey(key);
    } catch (RestApiException e) {
      throw new RuntimeException(e);
    }
  }

  public GerritApi api() {
    return gerritApi;
  }

  public String createProject() {
    try {
      String projectName = UUID.randomUUID().toString();
      gerritApi.projects().create(projectName);
      return projectName;
    } catch (RestApiException e) {
      throw new RuntimeException(e);
    }
  }

  private void stop() {
    container.stop();
  }
}
