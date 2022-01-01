package org.jenkinsci.plugins.sonargerrit.test_infrastructure.docker_network;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testcontainers.containers.Network;

class DockerNetworkTestExtension implements BeforeAllCallback, ParameterResolver {

  @Override
  public void beforeAll(ExtensionContext context) {
    ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
    if (store.get(Network.class) != null) {
      return;
    }
    Network network = Network.newNetwork();
    store.put(Network.class + "#close", (ExtensionContext.Store.CloseableResource) network::close);
    store.put(Network.class, network);
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return Network.class.equals(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    return extensionContext
        .getRoot()
        .getStore(ExtensionContext.Namespace.GLOBAL)
        .get(Network.class, Network.class);
  }
}
