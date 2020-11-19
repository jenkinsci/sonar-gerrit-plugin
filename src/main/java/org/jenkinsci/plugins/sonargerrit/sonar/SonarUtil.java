package org.jenkinsci.plugins.sonargerrit.sonar;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.sonargerrit.config.SonarInstallationReader;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import hudson.AbortException;
import hudson.model.ItemGroup;
import hudson.plugins.sonar.SonarInstallation;
import hudson.security.ACL;

public class SonarUtil {
    private static Pattern componentKeyPattern = Pattern.compile(".*\\((?<key>.*)\\)");

    /**
     * @param value (group:componentKey)
     * @return group:componentKey
     */
    public static String isolateComponentKey(String value) {
        Matcher matcher = componentKeyPattern.matcher(value);
        if (matcher.matches()) {
            return matcher.group("key");
        } else {
            return value;
        }
    }

    /**
     * Possible pattern: Name (key) or key
     *
     * @param sonarInstallationName name of SonarQube installation which should be used
     * @return client to access SonarQube
     * @throws AbortException if Sonar installation cannot be found
     */
    public static SonarClient getSonarClient(String sonarInstallationName) throws AbortException {
        SonarInstallation sonarInstallation = SonarInstallationReader.getSonarInstallation(sonarInstallationName);
        List<StringCredentials> stringCredentials = CredentialsProvider.lookupCredentials(
                StringCredentials.class,
                (ItemGroup<?>) null,
                ACL.SYSTEM,
                (DomainRequirement) null
        );
        StringCredentials credentials = CredentialsMatchers.firstOrNull(
                stringCredentials,
                CredentialsMatchers.withId(sonarInstallation.getCredentialsId())
        );

        if (credentials == null) {
            throw new IllegalStateException("Missing Server authentication token for SonarQube Server " + sonarInstallation.getName());
        }
        return new SonarClient(sonarInstallation, credentials);
    }
}
