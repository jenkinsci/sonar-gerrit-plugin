package org.jenkinsci.plugins.sonargerrit;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
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
        this.projectPath = projectPath;
        this.sonarReportPath = sonarReportPath;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getSonarReportPath() {
        return sonarReportPath;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<SubJobConfig> {
        public String getDisplayName() {
            return "SubJobConfig";
        }
    }

}


