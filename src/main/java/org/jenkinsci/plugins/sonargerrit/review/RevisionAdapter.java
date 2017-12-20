package org.jenkinsci.plugins.sonargerrit.review;

import java.util.Map;
import java.util.Set;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 19.12.2017 21:58
 */
public interface RevisionAdapter {

    Set<String> getChangedFiles();

    Map<String, Set<Integer>> getFileToChangedLines();

}
