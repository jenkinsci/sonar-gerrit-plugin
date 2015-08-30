package com.aquarellian.genar.data.entity;


import java.util.Date;

/**
 * @author Tatiana Didik (aquarellian@gmail.com)
 */
public class Issue {

    private String key;
    private String component;
    private Integer line;
    private String message;
    private Severity severity;
    private String rule;
    private String status;
    private Boolean isNew;
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

