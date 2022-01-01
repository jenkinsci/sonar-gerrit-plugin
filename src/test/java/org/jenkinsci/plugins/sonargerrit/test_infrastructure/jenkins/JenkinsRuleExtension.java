package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
class JenkinsRuleExtension implements BeforeAllCallback, ParameterResolver {

  @Override
  public void beforeAll(ExtensionContext context) {
    startJenkinsRule(context);
  }

  private void startJenkinsRule(ExtensionContext context) {
    ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
    store.getOrComputeIfAbsent(
        JenkinsRule.class,
        jenkinsRuleClass -> {
          JupiterJenkinsRule jenkinsRule = new JupiterJenkinsRule(context);
          try {
            jenkinsRule.before();
          } catch (Throwable e) {
            throw new RuntimeException(e);
          }
          return jenkinsRule;
        },
        JenkinsRule.class);
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    return JenkinsRule.class.equals(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context)
      throws ParameterResolutionException {

    return context
        .getRoot()
        .getStore(ExtensionContext.Namespace.GLOBAL)
        .get(JenkinsRule.class, JenkinsRule.class);
  }
}
