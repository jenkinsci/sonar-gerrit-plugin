package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 20:59
 * $Id$
 */
public class ScoreFilterSeverityNewChangedOnly extends FilterSeverityNewChangedOnly {
    @Override
    protected IssueFilterConfig getFilterConfig() {
        return publisher.getScoreConfig().getIssueFilterConfig();
    }
}
