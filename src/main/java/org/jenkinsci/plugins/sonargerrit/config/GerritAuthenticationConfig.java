package org.jenkinsci.plugins.sonargerrit.config;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritManagement;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer;
import com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.IGerritHudsonTriggerConfig;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 12.11.2017 21:53 $Id$ */
public class GerritAuthenticationConfig extends AuthenticationConfig {
  /** @deprecated Use {@link #GerritAuthenticationConfig(String, Secret)} */
  @Deprecated
  public GerritAuthenticationConfig(@Nonnull String username, @Nonnull String password) {
    super(username, password);
  }

  public GerritAuthenticationConfig(@Nonnull String username, @Nonnull Secret password) {
    super(username, password);
  }

  @DataBoundConstructor
  public GerritAuthenticationConfig() {
    super();
  }

  /** @deprecated Use {@link #setSecretPassword(Secret)} */
  @Deprecated
  @Override
  @DataBoundSetter
  public void setPassword(@Nonnull String password) {
    super.setPassword(password);
  }

  @Override
  @DataBoundSetter
  public void setSecretPassword(@Nonnull Secret password) {
    super.setSecretPassword(password);
  }

  @Override
  @DataBoundSetter
  public void setUsername(@Nonnull String username) {
    super.setUsername(username);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return new DescriptorImpl();
  }

  @Extension
  public static class DescriptorImpl extends AuthenticationConfig.DescriptorImpl {

    public FormValidation doTestConnection(
        @QueryParameter("username") final String username,
        @QueryParameter("password") final String password,
        @QueryParameter("serverName") final String serverName)
        throws IOException, ServletException {
      doCheckUsername(username);
      doCheckPassword(password);

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
              password /*, gerritConfig.isUseRestApi()*/);
    }

    public List<String> getServerNames() {
      return PluginImpl.getServerNames_();
    }

    public String getDisplayName() {
      return "GerritAuthenticationConfig";
    }
  }
}
