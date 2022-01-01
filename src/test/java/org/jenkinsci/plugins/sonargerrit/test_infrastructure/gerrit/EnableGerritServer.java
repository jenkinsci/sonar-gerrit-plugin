package org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.docker_network.EnableDockerNetwork;
import org.junit.jupiter.api.extension.ExtendWith;

/** @author Réda Housni Alaoui */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@EnableDockerNetwork
@ExtendWith(GerritTestExtension.class)
public @interface EnableGerritServer {}
