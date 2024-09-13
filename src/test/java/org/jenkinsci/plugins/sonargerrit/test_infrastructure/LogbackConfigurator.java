package org.jenkinsci.plugins.sonargerrit.test_infrastructure;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import java.util.Optional;

/** @author Réda Housni Alaoui */
public class LogbackConfigurator extends BasicConfigurator {

  @Override
  public ExecutionStatus configure(LoggerContext lc) {
    super.configure(lc);
    Level rootLevel =
        Optional.ofNullable(System.getenv("SONAR_GERRIT_PLUGIN_TEST_LOG_ROOT_LEVEL"))
            .map(Level::valueOf)
            .orElse(Level.OFF);
    lc.getLogger("ROOT").setLevel(rootLevel);
    lc.getLogger("com.sonyericsson.hudson").setLevel(Level.WARN);
    return ExecutionStatus.NEUTRAL;
  }
}
