package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 29.11.2017 15:21
 * $Id$
 */
public class SonarQubeIssueAdapter extends Issue implements IssueAdapter {

    private String filepath;

    private ComponentPathBuilder pathBuilder;

    private String projectPath;

    public SonarQubeIssueAdapter(Issue issue, ComponentPathBuilder pathBuilder, String projectPath) {
        super(issue);
        this.pathBuilder = pathBuilder;
        this.projectPath = projectPath;
    }

    @Override
    public String getFilepath() {
        if (filepath == null) {
            if (pathBuilder != null) {
                filepath = pathBuilder
                        .buildPrefixedPathForComponentWithKey(getComponent(), projectPath)
                        .or(getComponent());
            } else {
                filepath = getComponent();
            }
        }
        return filepath;
    }
}
