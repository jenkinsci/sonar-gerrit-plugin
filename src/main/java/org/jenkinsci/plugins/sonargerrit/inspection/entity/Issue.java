package org.jenkinsci.plugins.sonargerrit.inspection.entity;

import javax.json.bind.annotation.JsonbDateFormat;

import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */
public class Issue {

    public Issue() {
    }

    public Issue(Issue issue) {
        this.key = issue.key;
        this.component = issue.component;
        this.line = issue.line;
        this.message = issue.message;
        this.severity = issue.severity;
        this.rule = issue.rule;
        this.status = issue.status;
        this.isNew = issue.isNew;
        this.creationDate = issue.creationDate;
    }

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
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
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

    public void setKey(String key) {
        this.key = key;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public Boolean getNew() {
        return isNew;
    }

    public Date getCreationDate() {
        return new Date(creationDate.getTime());
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = new Date(creationDate.getTime());
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

