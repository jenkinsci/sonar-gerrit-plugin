package org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube;

import org.jenkinsci.plugins.sonargerrit.test_infrastructure.docker_network.DockerDependentTestExtension;

class SonarqubeTestExtension extends DockerDependentTestExtension<SonarqubeServer> {

  protected SonarqubeTestExtension() {
    super(SonarqubeServer.class, SonarqubeServer::start);
  }
}
