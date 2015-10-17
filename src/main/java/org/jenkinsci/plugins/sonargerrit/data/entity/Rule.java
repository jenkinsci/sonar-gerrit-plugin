package org.jenkinsci.plugins.sonargerrit.data.entity;


/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */

public class Rule {

    @SuppressWarnings(value = "unused")
    private String key;

    @SuppressWarnings(value = "unused")
    private String rule;

    @SuppressWarnings(value = "unused")
    private String repository;

    @SuppressWarnings(value = "unused")
    private String name;

    public String getKey() {
        return key;
    }

    public String getRule() {
        return rule;
    }

    public String getRepository() {
        return repository;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "key='" + key + '\'' +
                ", rule='" + rule + '\'' +
                ", repository='" + repository + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
