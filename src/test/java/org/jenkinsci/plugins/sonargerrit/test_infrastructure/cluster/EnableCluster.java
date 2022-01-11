package org.jenkinsci.plugins.sonargerrit.test_infrastructure.cluster;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit.EnableGerritServer;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.jenkins.EnableJenkinsRule;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.EnableSonarqube7Server;
import org.jenkinsci.plugins.sonargerrit.test_infrastructure.sonarqube.EnableSonarqubeServer;
import org.junit.jupiter.api.extension.ExtendWith;

/** @author Réda Housni Alaoui */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@EnableGerritServer
@EnableSonarqube7Server
@EnableSonarqubeServer
@EnableJenkinsRule
@ExtendWith(ClusterTestExtension.class)
public @interface EnableCluster {}
