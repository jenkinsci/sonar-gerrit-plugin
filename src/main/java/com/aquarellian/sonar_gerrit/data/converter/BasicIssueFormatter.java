package com.aquarellian.sonar_gerrit.data.converter;

import com.aquarellian.sonar_gerrit.data.entity.Issue;
import com.aquarellian.sonar_gerrit.data.entity.Severity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 03.09.2015 19:39
 * <p/>
 * $Id$
 */
public class BasicIssueFormatter implements IssueFormatter {
    private Issue issue;
    private String sonarUrl;

    public BasicIssueFormatter(Issue issue, String sonarUrl) {
        this.issue = issue;
        this.sonarUrl = sonarUrl;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(formatSeverity());
        sb.append(formatMessage());
        sb.append(formatRule());
        return sb.toString();
    }

    protected String formatMessage() {
        StringBuilder sb = new StringBuilder();
        String message = issue.getMessage();
        if (message != null) {
            sb.append(message);
            if (!message.endsWith(".")) {
                sb.append(".");
            }
            sb.append("\n\n\n");
        }
        return sb.toString();
    }

    protected String formatSeverity() {
        StringBuilder sb = new StringBuilder();
        Severity severity = issue.getSeverity();
        if (severity != null) {
            sb.append(severity.name()).append(" Sonar violation:");
            sb.append("\n\n\n");
        }
        return sb.toString();
    }

    protected String formatRule() {
        StringBuilder sb = new StringBuilder();
        String rule = issue.getRule();
        if (rule != null) {
            sb.append("Read more: ").append(getRuleLink(rule));
            sb.append("\n");
        }
        return sb.toString();
    }

    protected String getRuleLink(String rule){
        if (sonarUrl != null){
            StringBuilder sb = new StringBuilder();
            String url = sonarUrl.trim();
            if (!(url.startsWith("http://") || sonarUrl.startsWith("https://"))){
                sb.append("http://");
            }
            sb.append(url);
            if (!(url.endsWith("/"))){
                sb.append("/");
            }
            sb.append("coding_rules#q=");
            sb.append(escapeHttp(rule));      // squid%3AS1319
            return sb.toString();
        }
        return rule;
    }


    protected String escapeHttp(String query) {
//        return StringEscapeUtils.escapeHtml(query);     // todo this method does not escape semicolon. but is URLEncoder.encode a correct way to do so?
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return query;
        }
    }
}
