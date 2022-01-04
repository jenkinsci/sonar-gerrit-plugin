package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import static java.util.Objects.requireNonNull;

import com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.Config;
import java.util.UUID;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;

/** @author Réda Housni Alaoui */
public class GerritTriggerConfiguration {

  private final Jenkins jenkins;

  public GerritTriggerConfiguration(Jenkins jenkins) {
    this.jenkins = jenkins;
  }

  public String addServer(String url, String username, String password) {
    PluginImpl gerritTriggerPlugin =
        jenkins.getDescriptorList(GlobalConfiguration.class).get(PluginImpl.class);
    requireNonNull(gerritTriggerPlugin, "No gerrit trigger plugin instance found");
    gerritTriggerPlugin.load();

    String name = UUID.randomUUID().toString();

    com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer server =
        new com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer(name);
    Config config = new Config();
    config.setGerritHostName("localhost");
    config.setGerritFrontEndURL(url);
    config.setGerritUserName(username);
    config.setUseRestApi(true);
    config.setGerritHttpUserName(username);
    config.setGerritHttpPassword(password);
    server.setConfig(config);
    gerritTriggerPlugin.addServer(server);
    gerritTriggerPlugin.save();

    gerritTriggerPlugin.getServer(server.getName()).start();

    return name;
  }
}
