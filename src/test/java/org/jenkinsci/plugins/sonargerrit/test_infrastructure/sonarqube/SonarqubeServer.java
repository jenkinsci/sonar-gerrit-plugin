package org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.CloseableResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;

/** @author Réda Housni Alaoui */
public class SonarqubeServer {

  private static final Logger LOG = LoggerFactory.getLogger(SonarqubeServer.class);

  private static final int HTTP_PORT = 9000;

  private static final String NETWORK_ALIAS = "sonarqube";
  private static final String ADMIN_USERNAME = "admin";

  private final GenericContainer<?> container;
  private final String url;
  private final String adminPassword;

  public static CloseableResource<SonarqubeServer> start(Network network) {
    SonarqubeServer sonarqubeServer = new SonarqubeServer(network);
    return new CloseableResource<SonarqubeServer>() {
      @Override
      public SonarqubeServer resource() {
        return sonarqubeServer;
      }

      @Override
      public void close() {
        sonarqubeServer.stop();
      }
    };
  }

  private SonarqubeServer(Network network) {
    container =
        new GenericContainer<>(
                new ImageFromDockerfile()
                    .withDockerfileFromBuilder(
                        builder ->
                            builder
                                .from("sonarqube:8.9.2-community")
                                .add(
                                    "https://github.com/mc1arke/sonarqube-community-branch-plugin/releases/download/1.8.1/sonarqube-community-branch-plugin-1.8.1.jar",
                                    "/opt/sonarqube/extensions/plugins/sonarqube-community-branch-plugin.jar")))
            .withEnv(
                "SONAR_WEB_JAVAADDITIONALOPTS",
                "-javaagent:./extensions/plugins/sonarqube-community-branch-plugin.jar=web")
            .withEnv(
                "SONAR_CE_JAVAADDITIONALOPTS",
                "-javaagent:./extensions/plugins/sonarqube-community-branch-plugin.jar=ce")
            .withLogConsumer(new Slf4jLogConsumer(LOG))
            .withExposedPorts(HTTP_PORT)
            .withNetwork(network)
            .withNetworkAliases(NETWORK_ALIAS);
    container.start();
    url = "http://localhost:" + container.getMappedPort(HTTP_PORT);

    adminPassword = "1234";
    changeOwnPassword(url, ADMIN_USERNAME, "admin", adminPassword);
    authorizeThirdPartyPlugins(url, adminPassword);
  }

  public String url() {
    return url;
  }

  public String adminAuthorization() {
    return createAuthorization(ADMIN_USERNAME, adminPassword);
  }

  private void stop() {
    container.stop();
  }

  private static void changeOwnPassword(
      String sonarqubeUrl, String username, String currentPassword, String newPassword) {
    OkHttpClient httpClient = SonarqubeOkHttpClients.get();
    RequestBody requestBody =
        RequestBody.create(
            MediaType.parse("application/x-www-form-urlencoded"),
            "login="
                + username
                + "&previousPassword="
                + currentPassword
                + "&password="
                + newPassword);
    Request request =
        new Request.Builder()
            .url(sonarqubeUrl + "/api/users/change_password")
            .header("Authorization", createAuthorization(username, currentPassword))
            .post(requestBody)
            .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        return;
      }
      throw new IllegalStateException(
          "Own password change failed with code "
              + response.code()
              + " and message '"
              + response.message()
              + "'");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void authorizeThirdPartyPlugins(String sonarqubeUrl, String adminPassword) {
    OkHttpClient httpClient = SonarqubeOkHttpClients.get();
    RequestBody requestBody =
        RequestBody.create(
            MediaType.parse("application/x-www-form-urlencoded"),
            "key=sonar.plugins.risk.consent&value=ACCEPTED");
    Request request =
        new Request.Builder()
            .url(sonarqubeUrl + "/api/settings/set")
            .header("Authorization", createAuthorization(ADMIN_USERNAME, adminPassword))
            .post(requestBody)
            .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        return;
      }
      throw new IllegalStateException(
          "Third party plugins authorization failed with code "
              + response.code()
              + " and message '"
              + response.message()
              + "'");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String createAuthorization(String username, String password) {
    return "Basic "
        + Base64.getEncoder()
            .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
  }
}
