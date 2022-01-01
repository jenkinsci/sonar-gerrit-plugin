package org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.AnnotationUtils;
import org.jvnet.hudson.test.JenkinsRecipe;
import org.jvnet.hudson.test.JenkinsRule;

/** @author Réda Housni Alaoui */
class JupiterJenkinsRule extends JenkinsRule implements ExtensionContext.Store.CloseableResource {

  private final ExtensionContext context;

  JupiterJenkinsRule(ExtensionContext context) {
    this.context = context;
  }

  @Override
  public void recipe() throws Exception {
    JenkinsRecipe recipe =
        context
            .getElement()
            .flatMap(
                annotatedElement ->
                    AnnotationUtils.findAnnotation(annotatedElement, JenkinsRecipe.class))
            .orElse(null);
    if (recipe == null) {
      return;
    }
    @SuppressWarnings("unchecked")
    JenkinsRecipe.Runner<JenkinsRecipe> runner =
        (JenkinsRecipe.Runner<JenkinsRecipe>) recipe.value().getDeclaredConstructor().newInstance();
    recipes.add(runner);
    tearDowns.add(() -> runner.tearDown(this, recipe));
  }

  @Override
  public void close() throws Throwable {
    after();
  }
}
