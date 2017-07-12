package org.jenkinsci.plugins.sonargerrit.data.entity;



import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */
public class Report  {
    private String version;
    private List<Issue> issues;
    private List<Component> components;
    private List<Rule> rules;
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
