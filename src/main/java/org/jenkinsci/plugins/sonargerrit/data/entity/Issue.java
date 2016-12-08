package org.jenkinsci.plugins.sonargerrit.data.entity;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Date;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */
public class Issue {

    @SuppressWarnings("unused")
    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
    private String key;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String component;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private Integer line;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String message;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private Severity severity;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String rule;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String status;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private Boolean isNew;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
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
        return new Date(creationDate.getTime());
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

