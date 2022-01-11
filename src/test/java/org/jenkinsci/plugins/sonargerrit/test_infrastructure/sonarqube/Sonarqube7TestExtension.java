package org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube;

import org.jenkinsci.plugins.sonargerrit.test_infrastructure.docker_network.DockerDependentTestExtension;

class Sonarqube7TestExtension extends DockerDependentTestExtension<Sonarqube7Server> {

  protected Sonarqube7TestExtension() {
    super(Sonarqube7Server.class, Sonarqube7Server::start);
  }
}
