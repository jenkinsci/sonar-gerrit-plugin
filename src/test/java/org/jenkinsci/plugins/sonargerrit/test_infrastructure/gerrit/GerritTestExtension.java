package org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit;

import org.jenkinsci.plugins.sonargerrit.test_infrastructure.docker_network.DockerDependentTestExtension;

class GerritTestExtension extends DockerDependentTestExtension<GerritServer> {

  protected GerritTestExtension() {
    super(GerritServer.class, GerritServer::start);
  }
}
