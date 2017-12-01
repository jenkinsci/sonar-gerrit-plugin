package org.jenkinsci.plugins.sonargerrit.inspection.sonarqube;

import org.jenkinsci.plugins.sonargerrit.inspection.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Severity;

import java.util.Date;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 29.11.2017 15:21
 * $Id$
 */
public class SonarQubeIssue extends Issue {
    private Issue issue;

    private String filepath;

    public SonarQubeIssue(Issue issue, String filepath) {
        this.filepath = filepath;
        this.issue = issue;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public String getKey() {
        return issue.getKey();
    }

    @Override
    public String getComponent() {
        return issue.getComponent();
    }

    @Override
    public Integer getLine() {
        return issue.getLine();
    }

    @Override
    public String getMessage() {
        return issue.getMessage();
    }

    @Override
    public Severity getSeverity() {
        return issue.getSeverity();
    }

    @Override
    public String getRule() {
        return issue.getRule();
    }

    @Override
    public String getStatus() {
        return issue.getStatus();
    }

    @Override
    public boolean isNew() {
        return issue.isNew();
    }

    @Override
    public Date getCreationDate() {
        return issue.getCreationDate();
    }

    @Override
    public String toString() {
        return issue.toString() + " real path: " + filepath;
    }
}
