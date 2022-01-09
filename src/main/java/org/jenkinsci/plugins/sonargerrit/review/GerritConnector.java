package org.jenkinsci.plugins.sonargerrit.review;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.google.common.base.MoreObjects;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritManagement;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.IGerritHudsonTriggerConfig;
import me.redaalaoui.gerrit_rest_java_client.rest.GerritAuthData;
import me.redaalaoui.gerrit_rest_java_client.rest.GerritRestApiFactory;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.GerritApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.jenkinsci.plugins.sonargerrit.util.DataHelper;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 20.11.2017 16:19
 *
 * <p>$Id$
 */
@Restricted(NoExternalUse.class)
public class GerritConnector {

  private final ConnectionInfo connectionInfo;
  private final GerritApi gerritApi;

  private GerritConnector(GerritConnectionInfo connectionInfo) {
    this.connectionInfo = connectionInfo;

    String serverName = connectionInfo.getServerName();

    IGerritHudsonTriggerConfig gerritConfig = GerritManagement.getConfig(serverName);
    DataHelper.checkNotNull(gerritConfig, "jenkins.plugin.error.gerrit.config.empty");

    String gerritFrontEndUrl = gerritConfig.getGerritFrontEndUrl();

    checkRestApiAllowed(gerritConfig.isUseRestApi());

    String username = getUsername(connectionInfo.getUsername(), gerritConfig);
    String password = getPassword(connectionInfo.getPassword(), gerritConfig);
    DataHelper.checkNotEmpty(username, "jenkins.plugin.error.gerrit.user.empty");

    GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
    GerritAuthData.Basic authData =
        new GerritAuthData.Basic(gerritFrontEndUrl, username, password, true);
    gerritApi = gerritRestApiFactory.create(authData);
  }

  public static GerritConnector connect(GerritConnectionInfo connectionInfo) {
    return new GerritConnector(connectionInfo);
  }

  public GerritRevision fetchRevision() throws RestApiException {
    return GerritRevision.load(
        gerritApi
            .changes()
            .id(connectionInfo.getChangeNumber())
            .revision(connectionInfo.getPatchsetNumber()));
  }

  private void checkRestApiAllowed(boolean useRestApi) {
    if (!useRestApi) {
      throw new IllegalStateException(getLocalized("jenkins.plugin.error.gerrit.restapi.off"));
    }
  }

  private String getUsername(String username, IGerritHudsonTriggerConfig gerritConfig) {
    return MoreObjects.firstNonNull(username, gerritConfig.getGerritHttpUserName());
  }

  private String getPassword(String password, IGerritHudsonTriggerConfig gerritConfig) {
    return MoreObjects.firstNonNull(password, gerritConfig.getGerritHttpPassword());
  }
}
