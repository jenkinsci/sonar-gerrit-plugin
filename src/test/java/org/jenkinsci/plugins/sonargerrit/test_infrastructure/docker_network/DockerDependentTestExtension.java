package org.jenkinsci.plugins.sonargerrit.test_infrastructure.docker_network;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.CloseableResource;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testcontainers.containers.Network;

/** @author Réda Housni Alaoui */
public abstract class DockerDependentTestExtension<T>
    implements BeforeAllCallback, ParameterResolver {

  private final Class<T> type;
  private final Function<Network, CloseableResource<T>> factory;

  protected DockerDependentTestExtension(
      Class<T> type, Function<Network, CloseableResource<T>> factory) {
    this.type = requireNonNull(type);
    this.factory = requireNonNull(factory);
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
    if (store.get(type) != null) {
      return;
    }
    CloseableResource<T> server = factory.apply(store.get(Network.class, Network.class));
    store.put(type + "#close", (ExtensionContext.Store.CloseableResource) server::close);
    store.put(type, server.resource());
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return type.equals(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    return extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).get(type, type);
  }
}
