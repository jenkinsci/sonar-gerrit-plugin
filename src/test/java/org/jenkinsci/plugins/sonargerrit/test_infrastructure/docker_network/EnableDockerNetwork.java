package org.jenkinsci.plugins.sonargerrit.test_infrastructure.docker_network;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ExtendWith(DockerNetworkTestExtension.class)
public @interface EnableDockerNetwork {}
