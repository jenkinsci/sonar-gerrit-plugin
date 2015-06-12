package com.aquarellian.genar.data.entity;



import java.util.Date;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 10:31
 * <p/>
 * $Id$
 */

//todo javadoc
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

    public Boolean isNew() {
        return isNew != null && isNew;
    }

    public Date getCreationDate(){return creationDate;}

    @SuppressWarnings("RedundantIfStatement") //NOSONAR
    @Override
    public boolean equals(Object o) { //NOSONAR
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issue issue = (Issue) o;

        if (component != null ? !component.equals(issue.component) : issue.component != null)
            return false;
        if (isNew != null ? !isNew.equals(issue.isNew) : issue.isNew != null)
            return false;
        if (key != null ? !key.equals(issue.key) : issue.key != null)
            return false;
        if (line != null ? !line.equals(issue.line) : issue.line != null)
            return false;
        if (message != null ? !message.equals(issue.message) : issue.message != null)
            return false;
        if (rule != null ? !rule.equals(issue.rule) : issue.rule != null)
            return false;
        if (severity != null ? !severity.equals(issue.severity) : issue.severity != null)
            return false;
        if (status != null ? !status.equals(issue.status) : issue.status != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (component != null ? component.hashCode() : 0);
        result = 31 * result + (line != null ? line.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (isNew != null ? isNew.hashCode() : 0);
        return result;
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

