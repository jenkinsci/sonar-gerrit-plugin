package org.jenkinsci.plugins.sonargerrit.gerrit;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.cloudbees.plugins.credentials.common.PasswordCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernameCredentials;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger;
import hudson.model.Item;
import hudson.util.Secret;
import java.util.Map;
import java.util.Optional;
import org.jenkinsci.plugins.sonargerrit.util.DataHelper;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 28.11.2017 16:33
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public class GerritConnectionInfo {

  public static final String GERRIT_NAME_ENV_VAR_NAME = "GERRIT_NAME";
  public static final String GERRIT_CHANGE_NUMBER_ENV_VAR_NAME = "GERRIT_CHANGE_NUMBER";
  public static final String GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME = "GERRIT_PATCHSET_NUMBER";

  private final String serverName;
  private final String changeNumber;
  private final String patchsetNumber;
  private final StandardUsernamePasswordCredentials httpCredentials;

  public GerritConnectionInfo(
      Map<String, String> envVars,
      GerritTrigger trigger,
      GerritAuthenticationConfig authenticationConfig,
      Item context) {
    serverName = retrieveServerName(envVars, trigger);

    changeNumber = retrieveChangeNumber(envVars);
    patchsetNumber = retrievePatchsetNumber(envVars);

    httpCredentials =
        Optional.ofNullable(authenticationConfig)
            .flatMap(
                gerritAuthenticationConfig ->
                    gerritAuthenticationConfig.getHttpCredentials(context))
            .orElse(null);
  }

  public String getServerName() {
    return serverName;
  }

  public String getChangeNumber() {
    return changeNumber;
  }

  public String getPatchsetNumber() {
    return patchsetNumber;
  }

  public String getUsername() {
    return Optional.ofNullable(httpCredentials).map(UsernameCredentials::getUsername).orElse(null);
  }

  public String getPassword() {
    return Optional.ofNullable(httpCredentials)
        .map(PasswordCredentials::getPassword)
        .map(Secret::getPlainText)
        .orElse(null);
  }

  private String retrieveServerName(Map<String, String> envVars, GerritTrigger trigger) {
    String serverName = envVars.get(GERRIT_NAME_ENV_VAR_NAME);
    String triggerServerName = trigger != null ? trigger.getServerName() : null;
    serverName = Optional.ofNullable(serverName).orElse(triggerServerName);
    checkServerName(serverName);
    return serverName;
  }

  private String retrieveChangeNumber(Map<String, String> envVars) {
    String changeNum = envVars.get(GERRIT_CHANGE_NUMBER_ENV_VAR_NAME);
    DataHelper.checkNotEmpty(
        changeNum, getLocalized("jenkins.plugin.error.gerrit.change.number.empty"));
    Integer changeNumber = DataHelper.parseNumber(changeNum);
    DataHelper.checkNotEmpty(
        changeNumber, getLocalized("jenkins.plugin.error.gerrit.change.number.format"));
    return changeNum;
  }

  private String retrievePatchsetNumber(Map<String, String> envVars) {
    String patchsetNum = envVars.get(GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME);
    DataHelper.checkNotEmpty(
        patchsetNum, getLocalized("jenkins.plugin.error.gerrit.patchset.number.empty"));
    Integer patchsetNumber = DataHelper.parseNumber(patchsetNum);
    DataHelper.checkNotEmpty(
        patchsetNumber, getLocalized("jenkins.plugin.error.gerrit.patchset.number.format"));
    return patchsetNum;
  }

  private void checkServerName(String serverName) {
    DataHelper.checkNotEmpty(serverName, getLocalized("jenkins.plugin.error.gerrit.server.empty"));
  }
}
