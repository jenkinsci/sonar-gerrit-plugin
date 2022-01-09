package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.cloudbees.plugins.credentials.common.PasswordCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernameCredentials;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritManagement;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer;
import com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.IGerritHudsonTriggerConfig;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import java.util.List;
import java.util.Optional;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 12.11.2017 21:43 $Id$ */
public class GerritAuthenticationConfig
    extends AbstractDescribableImpl<GerritAuthenticationConfig> {

  // Only kept for backward compatibility purpose
  private transient String username;

  // Only kept for backward compatibility purpose
  private transient Secret password;

  private String httpCredentialsId;

  public GerritAuthenticationConfig() {
    this(null);
  }

  public GerritAuthenticationConfig(@Nullable String httpCredentialsId) {
    this.httpCredentialsId = httpCredentialsId;
    this.username = null;
    this.password = null;
  }

  /** @deprecated Use {@link #GerritAuthenticationConfig(String)} instead */
  @Deprecated
  @DataBoundConstructor
  public GerritAuthenticationConfig(
      @Nullable String httpCredentialsId,
      @Nullable String username,
      @Nullable Secret secretPassword,
      @Nullable String password) {

    this.username = null;
    this.password = null;

    if (Util.fixEmpty(username) == null
        && secretPassword == null
        && Util.fixEmpty(password) == null) {
      this.httpCredentialsId = httpCredentialsId;
      return;
    }

    Secret passwordToUse =
        Optional.ofNullable(secretPassword).orElseGet(() -> Secret.fromString(password));

    this.httpCredentialsId =
        GerritHttpCredentials.get().migrate(username, passwordToUse).orElse(null);
  }

  @SuppressWarnings("unused")
  protected Object readResolve() {
    if (username != null || password != null) {
      httpCredentialsId = GerritHttpCredentials.get().migrate(username, password).orElse(null);
      this.username = null;
      this.password = null;
    }
    return this;
  }

  @SuppressWarnings("unused")
  @Nullable
  public String getHttpCredentialsId() {
    return httpCredentialsId;
  }

  @Restricted(NoExternalUse.class)
  public GerritAuthenticationConfig withUsernamePassword(
      @Nullable String username, @Nullable String password) {
    return new GerritAuthenticationConfig(
        httpCredentialsId, username, Secret.fromString(password), null);
  }

  /** @deprecated Use {@link #getHttpCredentialsId()} instead */
  @Deprecated
  @Nullable
  public String getUsername() {
    return getHttpCredentials(null).map(UsernameCredentials::getUsername).orElse(null);
  }

  /** @deprecated Use {@link #getHttpCredentialsId()} instead */
  @Deprecated
  @Nullable
  public String getPassword() {
    return getHttpCredentials(null)
        .map(PasswordCredentials::getPassword)
        .map(Secret::getPlainText)
        .map(Util::fixEmpty)
        .orElse(null);
  }

  /** @deprecated Use {@link #getHttpCredentialsId()} instead */
  @Deprecated
  public Secret getSecretPassword() {
    return getHttpCredentials(null).map(PasswordCredentials::getPassword).orElse(null);
  }

  public Optional<StandardUsernamePasswordCredentials> getHttpCredentials(
      @Nullable Item requester) {
    return GerritHttpCredentials.get().findById(requester, httpCredentialsId);
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<GerritAuthenticationConfig> {

    @SuppressWarnings(value = "unused")
    public ListBoxModel doFillHttpCredentialsIdItems(
        @AncestorInPath Item item, @QueryParameter String credentialsId) {
      return GerritHttpCredentials.get().listCredentials(item, credentialsId);
    }

    @SuppressWarnings(value = "unused")
    public FormValidation doTestConnection(
        @AncestorInPath Item item,
        @QueryParameter("httpCredentialsId") final String httpCredentialsId,
        @QueryParameter("serverName") final String serverName) {
      FormValidation credentialsIdRequiredValidation =
          FormValidation.validateRequired(httpCredentialsId);
      if (credentialsIdRequiredValidation.kind == FormValidation.Kind.ERROR) {
        return credentialsIdRequiredValidation;
      }

      StandardUsernamePasswordCredentials credentials =
          GerritHttpCredentials.get().findById(item, httpCredentialsId).orElse(null);
      if (credentials == null) {
        return FormValidation.error(
            getLocalized("jenkins.plugin.error.gerrit.http.credentials.id.not-found"));
      }

      IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(serverName);
      if (gerritConfig == null) {
        return FormValidation.error(getLocalized("jenkins.plugin.error.gerrit.config.empty"));
      }

      if (!gerritConfig.isUseRestApi()) {
        return FormValidation.error(getLocalized("jenkins.plugin.error.gerrit.restapi.off"));
      }

      GerritServer server = PluginImpl.getServer_(serverName);
      if (server == null) {
        return FormValidation.error(getLocalized("jenkins.plugin.error.gerrit.server.empty"));
      }

      return server
          .getDescriptor()
          .doTestRestConnection(
              gerritConfig.getGerritFrontEndUrl(),
              credentials.getUsername(),
              credentials.getPassword().getPlainText());
    }

    @SuppressWarnings(value = "unused")
    public List<String> getServerNames() {
      return PluginImpl.getServerNames_();
    }

    @Override
    public String getDisplayName() {
      return "GerritAuthenticationConfig";
    }
  }
}
