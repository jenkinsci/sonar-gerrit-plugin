package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import java.util.ArrayList;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** @author Réda Housni Alaoui */
class PreviewModeAnalysisStrategyTest {

  private static final String SONAR_URL = SonarToGerritPublisher.DescriptorImpl.SONAR_URL;
  private static final String SONAR_REPORT_PATH =
      SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH;
  private static final String PROJECT_PATH = SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH;
  private static final String DEFAULT_INSPECTION_CONFIG_TYPE =
      PreviewModeAnalysisStrategy.DescriptorImpl.DEFAULT_INSPECTION_CONFIG_TYPE;
  private static final boolean PATH_AUTO_MATCH =
      PreviewModeAnalysisStrategy.DescriptorImpl.AUTO_MATCH;

  @Test
  @DisplayName("Non default values")
  void test1() {
    PreviewModeAnalysisStrategy strategy = new PreviewModeAnalysisStrategy();
    Assertions.assertEquals(SONAR_REPORT_PATH, strategy.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, strategy.getBaseConfig().getProjectPath());
    Assertions.assertEquals(PATH_AUTO_MATCH, strategy.getBaseConfig().isAutoMatch());
    Assertions.assertTrue(strategy.isType(DEFAULT_INSPECTION_CONFIG_TYPE));
    Assertions.assertEquals(1, strategy.getSubJobConfigs().size());
    SubJobConfig subJobConfig = new ArrayList<>(strategy.getSubJobConfigs()).get(0);
    Assertions.assertEquals(SONAR_REPORT_PATH, subJobConfig.getSonarReportPath());
    Assertions.assertEquals(PROJECT_PATH, subJobConfig.getProjectPath());
    Assertions.assertEquals(PATH_AUTO_MATCH, subJobConfig.isAutoMatch());
    Assertions.assertEquals(strategy.isAutoMatch(), strategy.getBaseConfig().isAutoMatch());
    Assertions.assertFalse(strategy.isMultiConfigMode());
    Assertions.assertEquals(
        strategy.getBaseConfig(), new ArrayList<>(strategy.getAllSubJobConfigs()).get(0));

    Assertions.assertNotSame("Test1", SONAR_URL);
    Assertions.assertNotSame("Test2", SONAR_REPORT_PATH);
    Assertions.assertNotSame("Test3", PROJECT_PATH);
    Assertions.assertNotSame(true, PATH_AUTO_MATCH);
    Assertions.assertNotSame("multi", DEFAULT_INSPECTION_CONFIG_TYPE);
    strategy.getBaseConfig().setSonarReportPath("Test2");
    strategy.getBaseConfig().setProjectPath("Test3");

    strategy.setAutoMatch(true);
    Assertions.assertTrue(strategy.getBaseConfig().isAutoMatch());
    Assertions.assertEquals(strategy.isAutoMatch(), strategy.getBaseConfig().isAutoMatch());
    strategy.setType("multi");
    Assertions.assertNotSame(strategy.isAutoMatch(), strategy.getBaseConfig().isAutoMatch());

    Assertions.assertEquals("Test2", strategy.getBaseConfig().getSonarReportPath());
    Assertions.assertEquals("Test3", strategy.getBaseConfig().getProjectPath());
    Assertions.assertTrue(strategy.isMultiConfigMode());
    Assertions.assertNotSame(
        strategy.getBaseConfig(), new ArrayList<>(strategy.getAllSubJobConfigs()).get(0));
    Assertions.assertEquals(
        new ArrayList<>(strategy.getSubJobConfigs()).get(0),
        new ArrayList<>(strategy.getAllSubJobConfigs()).get(0));
  }

  @Test
  @DisplayName("Wrong values")
  void test2() {
    PreviewModeAnalysisStrategy strategy = new PreviewModeAnalysisStrategy();
    strategy.setType("test");
    Assertions.assertFalse(strategy.isType("test"));
    Assertions.assertTrue(strategy.isType(DEFAULT_INSPECTION_CONFIG_TYPE));

    strategy.setType("multi");
    Assertions.assertTrue(strategy.isType("multi"));
    strategy.setType("test");
    Assertions.assertFalse(strategy.isType("test"));
    Assertions.assertTrue(strategy.isType("multi"));
  }

  @Test
  @DisplayName("Default values")
  void test3() {
    Assertions.assertFalse(new PreviewModeAnalysisStrategy().isMultiConfigMode());
  }
}
