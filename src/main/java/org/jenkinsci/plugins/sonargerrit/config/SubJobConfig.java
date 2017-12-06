package org.jenkinsci.plugins.sonargerrit.config;

import com.google.common.base.MoreObjects;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher;
import org.jenkinsci.plugins.sonargerrit.util.DataHelper;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 02.12.2015 12:11
 *
 */

public class SubJobConfig extends AbstractDescribableImpl<SubJobConfig> {
    private String projectPath;
    private String sonarReportPath;

    @DataBoundConstructor
    public SubJobConfig(String projectPath, String sonarReportPath) {
        setProjectPath(projectPath);
        setSonarReportPath(sonarReportPath);
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getSonarReportPath() {
        return sonarReportPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = MoreObjects.firstNonNull(projectPath, SonarToGerritPublisher.DescriptorImpl.PROJECT_PATH);
    }

    public void setSonarReportPath(String sonarReportPath) {
        this.sonarReportPath = MoreObjects.firstNonNull(sonarReportPath, SonarToGerritPublisher.DescriptorImpl.SONAR_REPORT_PATH);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<SubJobConfig> {
        public String getDisplayName() {
            return "SubJobConfig";
        }
    }

}


