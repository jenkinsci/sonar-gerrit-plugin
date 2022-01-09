package org.jenkinsci.plugins.sonargerrit.gerrit;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** @author Réda Housni Alaoui */
@Restricted(NoExternalUse.class)
class GerritHttpCredentials {

  private static final String PLUGIN_AUTHORED_CREDENTIALS_ID_PREFIX = "sonar-gerrit:";

  private GerritHttpCredentials() {}

  public static GerritHttpCredentials get() {
    return new GerritHttpCredentials();
  }

  /** @return Empty if username and password are null */
  public Optional<String> migrate(String username, Secret password) {
    if (username == null && password == null) {
      return Optional.empty();
    }
    StandardUsernamePasswordCredentials credentials =
        findPluginAuthored(username, password).orElseGet(() -> create(username, password));
    return Optional.of(credentials.getId());
  }

  public Optional<StandardUsernamePasswordCredentials> findById(Item item, String credentialsId) {
    if (credentialsId == null) {
      return Optional.empty();
    }

    StandardUsernamePasswordCredentials credentials =
        CredentialsMatchers.firstOrNull(
            CredentialsProvider.lookupCredentials(
                StandardUsernamePasswordCredentials.class,
                item,
                ACL.SYSTEM,
                Collections.emptyList()),
            CredentialsMatchers.withId(credentialsId));
    return Optional.ofNullable(credentials);
  }

  public ListBoxModel listCredentials(Item item, String currentCredentialsId) {
    StandardListBoxModel result = new StandardListBoxModel();
    if (item == null) {
      if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
        return result.includeCurrentValue(currentCredentialsId);
      }
    } else {
      if (!item.hasPermission(Item.EXTENDED_READ)
          && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
        return result.includeCurrentValue(currentCredentialsId);
      }
    }
    return result
        .includeEmptyValue()
        .includeMatchingAs(
            ACL.SYSTEM,
            item,
            StandardUsernamePasswordCredentials.class,
            Collections.emptyList(),
            CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class))
        .includeCurrentValue(currentCredentialsId);
  }

  private Optional<StandardUsernamePasswordCredentials> findPluginAuthored(
      String username, Secret password) {

    StandardUsernamePasswordCredentials credentials =
        CredentialsMatchers.firstOrNull(
            CredentialsProvider.lookupCredentials(
                StandardUsernamePasswordCredentials.class,
                (Item) null,
                ACL.SYSTEM,
                Collections.emptyList()),
            new PluginAuthoredCredentialsMatcher(username, password));

    return Optional.ofNullable(credentials);
  }

  public StandardUsernamePasswordCredentials create(String username, Secret password) {
    SystemCredentialsProvider credentialsProvider = SystemCredentialsProvider.getInstance();
    String credentialsId = PLUGIN_AUTHORED_CREDENTIALS_ID_PREFIX + UUID.randomUUID();
    String passwordPlainText = Optional.ofNullable(password).map(Secret::getPlainText).orElse(null);
    credentialsProvider
        .getCredentials()
        .add(
            new UsernamePasswordCredentialsImpl(
                CredentialsScope.GLOBAL, credentialsId, null, username, passwordPlainText));
    try {
      credentialsProvider.save();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return findById(null, credentialsId)
        .orElseThrow(
            () -> new IllegalStateException("Could not find credentials for id " + credentialsId));
  }

  private static class PluginAuthoredCredentialsMatcher implements CredentialsMatcher {

    private final String username;
    private final Secret password;

    PluginAuthoredCredentialsMatcher(String username, Secret password) {
      this.username = username;
      this.password = Optional.ofNullable(password).orElseGet(() -> Secret.fromString(null));
    }

    @Override
    public boolean matches(@NonNull Credentials item) {
      if (!(item instanceof StandardUsernamePasswordCredentials)) {
        return false;
      }
      StandardUsernamePasswordCredentials credentials = (StandardUsernamePasswordCredentials) item;
      if (!credentials.getId().startsWith(PLUGIN_AUTHORED_CREDENTIALS_ID_PREFIX)) {
        return false;
      }
      return credentials.getUsername().equals(username)
          && credentials.getPassword().equals(password);
    }
  }
}
