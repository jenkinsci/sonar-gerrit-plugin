package org.jenkinsci.plugins.sonargerrit.config;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritManagement;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer;
import com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.IGerritHudsonTriggerConfig;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.Secret;
import java.util.List;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 12.11.2017 21:43 $Id$ */
public class GerritAuthenticationConfig
    extends AbstractDescribableImpl<GerritAuthenticationConfig> {

  /*
   * Gerrit http username if overridden (the original one is in Gerrit Trigger settings)
   * */
  private String username;

  /*
   * Gerrit http password if overridden (the original one is in Gerrit Trigger settings)
   * */
  private Secret password;

  public String getUsername() {
    return username;
  }

  @DataBoundSetter
  public void setUsername(String username) {
    this.username = username;
  }

  /** @deprecated Use {@link #getSecretPassword()} */
  @Deprecated
  public String getPassword() {
    Secret pass = getSecretPassword();
    if (pass == null) {
      return null;
    }
    return pass.getPlainText();
  }

  /** @deprecated Use {@link #setSecretPassword(Secret)} */
  @Deprecated
  @DataBoundSetter
  public void setPassword(String password) {
    setSecretPassword(Secret.fromString(password));
  }

  public Secret getSecretPassword() {
    return password;
  }

  @DataBoundSetter
  public void setSecretPassword(Secret password) {
    this.password = password;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<GerritAuthenticationConfig> {
    /**
     * Performs on-the-fly validation of the form field 'username'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckUsername(@QueryParameter String value) {
      return FormValidation.validateRequired(value);
    }

    /**
     * Performs on-the-fly validation of the form field 'secretPassword'.
     *
     * @param value This parameter receives the value that the user has typed.
     * @return Indicates the outcome of the validation. This is sent to the browser.
     *     <p>Note that returning {@link FormValidation#error(String)} does not prevent the form
     *     from being saved. It just means that a message will be displayed to the user.
     */
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckSecretPassword(@QueryParameter Secret value) {
      if (value == null) {
        return FormValidation.validateRequired(null);
      }
      return FormValidation.validateRequired(value.getPlainText());
    }

    @SuppressWarnings(value = "unused")
    public FormValidation doTestConnection(
        @QueryParameter("username") final String username,
        @QueryParameter("secretPassword") final Secret password,
        @QueryParameter("serverName") final String serverName) {
      FormValidation usernameValidation = doCheckUsername(username);
      if (usernameValidation.kind == FormValidation.Kind.ERROR) {
        return usernameValidation;
      }
      FormValidation passwordValidation = doCheckSecretPassword(password);
      if (passwordValidation.kind == FormValidation.Kind.ERROR) {
        return passwordValidation;
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
              username,
              password.getPlainText() /*, gerritConfig.isUseRestApi()*/);
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
