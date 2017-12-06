package org.jenkinsci.plugins.sonargerrit.inspection.entity;



import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */
public class Report  {
    @SuppressWarnings(value="unused")
    private String version;

    @SuppressWarnings(value="unused")
    private List<Issue> issues;

    @SuppressWarnings(value="unused")
    private List<Component> components;

    @SuppressWarnings(value="unused")
    private List<Rule> rules;

    @SuppressWarnings(value="unused")
    private List<User> users;

    public String getVersion() {
        return version;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "Report{" +
                "version='" + version + '\'' +
                ", issues=" + issues +
                ", components=" + components +
                ", rules=" + rules +
                ", users=" + users +
                '}';
    }

}
