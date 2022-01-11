package org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster;

import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.GerritServer;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.Sonarqube7Server;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.SonarqubeServer;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
class ClusterTestExtension implements BeforeAllCallback, ParameterResolver {
  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
    if (store.get(Cluster.class) != null) {
      return;
    }

    GerritServer gerritServer = store.get(GerritServer.class, GerritServer.class);
    Sonarqube7Server sonarqube7Server = store.get(Sonarqube7Server.class, Sonarqube7Server.class);
    SonarqubeServer sonarqubeServer = store.get(SonarqubeServer.class, SonarqubeServer.class);
    JenkinsRule jenkinsRule = store.get(JenkinsRule.class, JenkinsRule.class);

    store.put(
        Cluster.class,
        Cluster.configure(gerritServer, sonarqube7Server, sonarqubeServer, jenkinsRule));
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return Cluster.class.equals(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return extensionContext
        .getRoot()
        .getStore(ExtensionContext.Namespace.GLOBAL)
        .get(Cluster.class, Cluster.class);
  }
}
