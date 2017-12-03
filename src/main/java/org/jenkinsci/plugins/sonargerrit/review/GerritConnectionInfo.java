package org.jenkinsci.plugins.sonargerrit.review;

import com.google.common.base.MoreObjects;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger;
import org.jenkinsci.plugins.sonargerrit.config.AuthenticationConfig;
import org.jenkinsci.plugins.sonargerrit.util.DataHelper;

import java.util.Map;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 16:33
 * <p>
 * $Id$
 */
public class GerritConnectionInfo implements ConnectionInfo {

    public static final String GERRIT_NAME_ENV_VAR_NAME = "GERRIT_NAME";
    public static final String GERRIT_CHANGE_NUMBER_ENV_VAR_NAME = "GERRIT_CHANGE_NUMBER";
    public static final String GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME = "GERRIT_PATCHSET_NUMBER";

    public static final String[] REQUIRED_VARS = new String[]{GERRIT_NAME_ENV_VAR_NAME, GERRIT_CHANGE_NUMBER_ENV_VAR_NAME, GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME};

    private final String serverName;
    private final String changeNumber;
    private final String patchsetNumber;
    private final String username;
    private final String password;

    public GerritConnectionInfo(Map<String, String> envVars, GerritTrigger trigger, AuthenticationConfig authenticationConfig) {
        serverName = retrieveServerName(envVars, trigger);

        changeNumber = retrieveChangeNumber(envVars);
        patchsetNumber = retrievePatchsetNumber(envVars);

        username = authenticationConfig != null ? authenticationConfig.getUsername() : null;
        password = authenticationConfig != null ? authenticationConfig.getPassword() : null;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public String getChangeNumber() {
        return changeNumber;
    }

    @Override
    public String getPatchsetNumber() {
        return patchsetNumber;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    private String retrieveServerName(Map<String, String> envVars, GerritTrigger trigger) {
        String serverName = envVars.get(GERRIT_NAME_ENV_VAR_NAME);
        String triggerServerName = trigger != null ? trigger.getServerName() : null;
        serverName = MoreObjects.firstNonNull(serverName, triggerServerName);
        checkServerName(serverName);
        return serverName;
    }

    private String retrieveChangeNumber(Map<String, String> envVars) {
        String changeNum = envVars.get(GERRIT_CHANGE_NUMBER_ENV_VAR_NAME);
        DataHelper.checkNotEmpty(changeNum, getLocalized("jenkins.plugin.error.gerrit.change.number.empty"));
        Integer changeNumber = DataHelper.parseNumber(changeNum);
        DataHelper.checkNotEmpty(changeNumber, getLocalized("jenkins.plugin.error.gerrit.change.number.format"));
        return changeNum;
    }

    private String retrievePatchsetNumber(Map<String, String> envVars) {
        String patchsetNum = envVars.get(GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME);
        DataHelper.checkNotEmpty(patchsetNum, getLocalized("jenkins.plugin.error.gerrit.patchset.number.empty"));
        Integer patchsetNumber = DataHelper.parseNumber(patchsetNum);
        DataHelper.checkNotEmpty(patchsetNumber, getLocalized("jenkins.plugin.error.gerrit.patchset.number.format"));
        return patchsetNum;
    }


    private void checkServerName(String serverName) {
        DataHelper.checkNotEmpty(serverName, getLocalized("jenkins.plugin.error.gerrit.server.empty"));
    }

}
