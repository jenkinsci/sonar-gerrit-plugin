package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.entity.Issue;


/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 03.09.2015 19:39
 * <p/>
 * $Id$
 */
public class BasicIssueFormatter implements IssueFormatter {
    private Issue issue;

    public BasicIssueFormatter(Issue issue) {
        this.issue = issue;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Severity: ").append(issue.getSeverity().name()).append("\n");
        sb.append("Rule: ").append(issue.getRule()).append("\n");
        sb.append("Status: ").append(issue.getStatus()).append("\n");
        sb.append("Message: ").append(issue.getMessage()).append("\n");
        return sb.toString();
    }
}
