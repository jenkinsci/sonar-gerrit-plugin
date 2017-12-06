package org.jenkinsci.plugins.sonargerrit.filter;

import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 15.11.2017 17:20
 * <p>
 * $Id$
 */
public class ScoreFilterSeverityAndNew extends FilterSeverityNewOnly{
    @Override
    protected IssueFilterConfig getFilterConfig(){
        return publisher.getScoreConfig().getIssueFilterConfig();
    }
}
