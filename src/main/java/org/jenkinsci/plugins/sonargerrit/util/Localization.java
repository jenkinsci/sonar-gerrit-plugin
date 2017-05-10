package org.jenkinsci.plugins.sonargerrit.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 22.09.2015 11:55
 */
public final class Localization {

    public static final String MESSAGES = "messages";

    public static final String CONFIG = "org.jenkinsci.plugins.sonargerrit.SonarToGerritPublisher.config";

    private Localization() {
    }

    public static String getLocalized(String s) {
        return getLocalizedFromBundle(s, MESSAGES);
    }

    public static String getLocalizedFromBundle(String s, String bundle) {
        ResourceBundle messages = ResourceBundle.getBundle(bundle);
        return messages.getString(s);
    }

    public static String getLocalized(String s, Object... params) {
        String string = getLocalized(s);
        return String.format(string, params);
    }

    public static String getLocalized(String s, Locale l) {
        ResourceBundle messages = ResourceBundle.getBundle(MESSAGES, l);
        return messages.getString(s);
    }
}
