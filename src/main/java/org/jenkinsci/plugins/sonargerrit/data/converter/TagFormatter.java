package org.jenkinsci.plugins.sonargerrit.data.converter;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 13:28
 * <p/>
 * $Id$
 */
public interface TagFormatter<E extends Enum> {
    String getValueToReplace(E tag);

    String getMessage();
}
