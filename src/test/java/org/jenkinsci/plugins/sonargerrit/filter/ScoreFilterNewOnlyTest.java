package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 15:09
 * $Id$
 */
public class ScoreFilterNewOnlyTest extends FilterNewOnlyTest {

    @Override
    protected IssueFilterConfig getFilterConfig(){
        return publisher.getScoreConfig().getIssueFilterConfig();
    }
}