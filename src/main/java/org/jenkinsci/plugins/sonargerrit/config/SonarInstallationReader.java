package org.jenkinsci.plugins.sonargerrit.config;

import org.jenkinsci.plugins.sonargerrit.util.Localization;

import java.util.Arrays;

import hudson.AbortException;
import hudson.plugins.sonar.SonarGlobalConfiguration;
import hudson.plugins.sonar.SonarInstallation;
import jenkins.model.GlobalConfiguration;

public class SonarInstallationReader {
    private SonarInstallationReader() {}

    public static SonarInstallation getSonarInstallation(String sonarInstallationName) throws AbortException {
        SonarGlobalConfiguration sonarGlobalConfiguration = GlobalConfiguration.all().get(SonarGlobalConfiguration.class);

        if (sonarGlobalConfiguration == null) {
            throw new AbortException(Localization.getLocalized("jenkins.plugin.error.sonar.config.missing"));
        }

        SonarInstallation[] installations = sonarGlobalConfiguration.getInstallations();

        if (installations.length == 0) {
            throw new AbortException(Localization.getLocalized("jenkins.plugin.error.sonar.server.missing"));
        }

        return Arrays.stream(installations)
                .filter(installation -> installation.getName().equals(sonarInstallationName)).findFirst()
                .orElseThrow(() ->
                        new AbortException(Localization.getLocalized("jenkins.plugin.error.sonar.server.notfound")));
    }
}
