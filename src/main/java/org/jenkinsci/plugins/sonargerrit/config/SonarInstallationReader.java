package org.jenkinsci.plugins.sonargerrit.config;

import java.util.Arrays;

import hudson.AbortException;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import jenkins.model.GlobalConfiguration;

public class SonarInstallationReader {
    public static SonarInstallation getSonarInstallation(String sonarInstallationName) throws AbortException {
        SonarGlobalConfiguration sonarGlobalConfiguration = GlobalConfiguration.all().get(SonarGlobalConfiguration.class);

        if (sonarGlobalConfiguration == null) {
            throw new AbortException("Missing SonarGlobalConfiguration -> Install SonarQube Scanner for Jenkins: "
                    + "https://plugins.jenkins.io/sonar/");
        }

        SonarInstallation[] installations = sonarGlobalConfiguration.getInstallations();

        if (installations.length == 0) {
            throw new AbortException("No SonarQube server found -> Add one in Jenkins system configuration - SonarQube servers");
        }

        return Arrays.stream(installations)
                .filter(installation -> installation.getName().equals(sonarInstallationName)).findFirst()
                .orElseThrow(() -> new AbortException("SonarQube '" + sonarInstallationName + "' not found -> Add it in Jenkins"
                        + " system configuration - SonarQube servers"));
    }
}
