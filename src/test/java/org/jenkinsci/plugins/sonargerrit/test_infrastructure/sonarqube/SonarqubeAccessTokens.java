package org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.ObjectMappers;

/** @author Réda Housni Alaoui */
public class SonarqubeAccessTokens {

  private final SonarqubeServer sonarqubeServer;

  public SonarqubeAccessTokens(SonarqubeServer sonarqubeServer) {
    this.sonarqubeServer = sonarqubeServer;
  }

  public String createAdminAccessToken(String name) {
    RequestBody requestBody =
        RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "");
    Request request =
        new Request.Builder()
            .url(sonarqubeServer.url() + "/api/user_tokens/generate?name=" + name)
            .header("Authorization", sonarqubeServer.adminAuthorization())
            .post(requestBody)
            .build();

    try (Response response = SonarqubeOkHttpClients.get().newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IllegalStateException(
            "Sonarqube admin access token generation failed with code "
                + response.code()
                + " and message '"
                + response.message()
                + "'");
      }
      ResponseBody responseBody = response.body();
      if (responseBody == null) {
        throw new IllegalStateException(
            "Sonarqube admin access token generation failed because no response body was found");
      }
      return ObjectMappers.get().readValue(responseBody.byteStream(), AccessToken.class).token;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class AccessToken {

    private final String token;

    public AccessToken(@JsonProperty("token") String token) {
      this.token = token;
    }
  }
}
