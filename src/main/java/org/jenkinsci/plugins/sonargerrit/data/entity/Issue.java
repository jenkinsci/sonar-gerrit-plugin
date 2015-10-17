package org.jenkinsci.plugins.sonargerrit.data.entity;


import java.util.Date;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */
public class Issue {

    @SuppressWarnings(value = "unused")
    private String key;

    @SuppressWarnings(value = "unused")
    private String component;

    @SuppressWarnings(value = "unused")
    private Integer line;

    @SuppressWarnings(value = "unused")
    private String message;

    @SuppressWarnings(value = "unused")
    private Severity severity;

    @SuppressWarnings(value = "unused")
    private String rule;

    @SuppressWarnings(value = "unused")
    private String status;

    @SuppressWarnings(value = "unused")
    private Boolean isNew;

    @SuppressWarnings(value = "unused")
    private Date creationDate;

    public String getKey() {
        return key;
    }

    public String getComponent() {
        return component;
    }

    public Integer getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getRule() {
        return rule;
    }

    public String getStatus() {
        return status;
    }

    public boolean isNew() {
        return isNew != null && isNew;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "key='" + key + '\'' +
                ", component='" + component + '\'' +
                ", line=" + line +
                ", message='" + message + '\'' +
                ", severity='" + severity + '\'' +
                ", rule='" + rule + '\'' +
                ", status='" + status + '\'' +
                ", isNew=" + isNew +
                '}';
    }

}

