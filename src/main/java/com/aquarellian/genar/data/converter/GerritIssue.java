package com.aquarellian.genar.data.converter;

import com.aquarellian.genar.data.entity.Issue;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 14.07.2015 14:59
 * <p/>
 * $Id$
 */
public class GerritIssue {
    private String comment;
    private Integer line;

    public GerritIssue (Issue issue){
        this.line = issue.getLine();
        this.comment = formatComment(issue);
    }

    protected String formatComment(Issue issue){
       return String.format("");   //todo format settings
    }
}
