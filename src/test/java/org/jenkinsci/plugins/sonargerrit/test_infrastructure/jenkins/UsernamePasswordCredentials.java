package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import static java.util.Objects.requireNonNull;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.model.Descriptor;
import java.io.IOException;
import java.util.UUID;
import jenkins.model.Jenkins;

/**
 * @author Réda Housni Alaoui
 */
public class UsernamePasswordCredentials {

  private final Jenkins jenkins;

  public UsernamePasswordCredentials(Jenkins jenkins) {
    this.jenkins = requireNonNull(jenkins);
  }

  public String create(String username, String password) {
    SystemCredentialsProvider credentialsProvider = SystemCredentialsProvider.getInstance();
    String id = UUID.randomUUID().toString();
    Credentials credentials;
    try {
      credentials =
          new UsernamePasswordCredentialsImpl(
              CredentialsScope.GLOBAL, id, null, username, password);
    } catch (Descriptor.FormException e) {
      throw new RuntimeException(e);
    }
    credentialsProvider.getCredentials().add(credentials);
    try {
      credentialsProvider.save();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return id;
  }
}
