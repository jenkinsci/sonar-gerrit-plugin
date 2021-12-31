package org.jenkinsci.plugins.sonargerrit.config;

import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.Secret;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 12.11.2017 21:43 $Id$ */
public abstract class AuthenticationConfig extends AbstractDescribableImpl<AuthenticationConfig> {

  /*
   * Gerrit http username if overridden (the original one is in Gerrit Trigger settings)
   * */
  private String username;

  /*
   * Gerrit http password if overridden (the original one is in Gerrit Trigger settings)
   * */
  private Secret password;

  /** @deprecated Use {@link #AuthenticationConfig(String, Secret)} */
  @Deprecated
  public AuthenticationConfig(String username, String password) {
    this(username, Secret.fromString(password));
  }

  public AuthenticationConfig(String username, Secret password) {
    this.username = username;
    this.password = password;
  }

  public AuthenticationConfig() {}

  public String getUsername() {
    return username;
  }

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
  public void setPassword(String password) {
    setSecretPassword(Secret.fromString(password));
  }

  public Secret getSecretPassword() {
    return password;
  }

  public void setSecretPassword(Secret password) {
    this.password = password;
  }

  @Override
  public abstract DescriptorImpl getDescriptor();

  public abstract static class DescriptorImpl extends Descriptor<AuthenticationConfig> {
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

    /** @deprecated Use {@link #doCheckSecretPassword(Secret)} */
    @Deprecated
    @SuppressWarnings(value = "unused")
    public FormValidation doCheckPassword(@QueryParameter String value) {
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
    public abstract FormValidation doTestConnection(
        @QueryParameter("username") final String username,
        @QueryParameter("secretPassword") final Secret password,
        @QueryParameter("serverName") final String serverName)
        throws IOException, ServletException;

    @SuppressWarnings(value = "unused")
    public abstract List<String> getServerNames();

    @Override
    public String getDisplayName() {
      return "AuthenticationConfig";
    }
  }
}
