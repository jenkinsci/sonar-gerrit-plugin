package org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
class SonarqubeOkHttpClients {

  private static final OkHttpClient OK_HTTP_CLIENT =
      new OkHttpClient.Builder()
          .retryOnConnectionFailure(true)
          .addInterceptor(new RetryInterceptor())
          .build();

  public static OkHttpClient get() {
    return OK_HTTP_CLIENT;
  }

  private static class RetryInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RetryInterceptor.class);

    private static final Set<Integer> RETRYABLE_RESPONSE_CODES =
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList(401, 404, 503)));
    private static final int MAX_ATTEMPTS = 60;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
      return interceptWithRetry(chain, 1);
    }

    private Response interceptWithRetry(Chain chain, int attemptNumber) throws IOException {
      Response response;
      try {
        response = chain.proceed(chain.request());
      } catch (IOException e) {
        if (attemptNumber > MAX_ATTEMPTS) {
          throw e;
        }
        return waitAndRetry("Received IOException. Retrying in 1 second.", chain, attemptNumber);
      }
      int responseCode = response.code();
      if (attemptNumber > MAX_ATTEMPTS || !RETRYABLE_RESPONSE_CODES.contains(responseCode)) {
        return response;
      }
      response.close();
      return waitAndRetry(
          "Received response code " + response.code() + ". Retrying in 1 second.",
          chain,
          attemptNumber);
    }

    private Response waitAndRetry(String message, Chain chain, int currentAttemptNumber)
        throws IOException {
      LOG.info(message);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
      return interceptWithRetry(chain, currentAttemptNumber + 1);
    }
  }
}
