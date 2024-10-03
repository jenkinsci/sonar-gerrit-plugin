package org.jenkinsci.plugins.sonargerrit.test_infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.testcontainers.containers.output.BaseConsumer;
import org.testcontainers.containers.output.OutputFrame;

// based on org.testcontainers.containers.output.Slf4jLogConsumer
public class JulLogConsumer extends BaseConsumer<JulLogConsumer> {
  private final Logger logger;
  private boolean separateOutputStreams;
  private String prefix = "";

  public JulLogConsumer(Logger logger) {
    this(logger, false);
  }

  public JulLogConsumer(Logger logger, boolean separateOutputStreams) {
    this.logger = logger;
    this.separateOutputStreams = separateOutputStreams;
  }

  public JulLogConsumer withPrefix(String prefix) {
    this.prefix = "[" + prefix + "]";
    return this;
  }

  public JulLogConsumer withSeparateOutputStreams() {
    this.separateOutputStreams = true;
    return this;
  }

  @Override
  public void accept(OutputFrame outputFrame) {
    OutputFrame.OutputType outputType = outputFrame.getType();

    String utf8String = outputFrame.getUtf8String().replaceAll("((\\r?\\n)|(\\r))$", "");

    switch (outputType) {
      case END:
        break;
      case STDOUT:
        if (separateOutputStreams) {
          logger.log(Level.INFO, () -> prefix.isEmpty() ? "" : (prefix + ": ") + utf8String);
        } else {
          logger.log(Level.INFO, () -> prefix + " " + outputType + ": " + utf8String);
        }
        break;
      case STDERR:
        if (separateOutputStreams) {
          logger.log(Level.SEVERE, () -> prefix.isEmpty() ? "" : (prefix + ": ") + utf8String);
        } else {
          logger.log(Level.INFO, () -> prefix + " " + outputType + ": " + utf8String);
        }
        break;
      default:
        throw new IllegalArgumentException("Unexpected outputType " + outputType);
    }
  }
}
