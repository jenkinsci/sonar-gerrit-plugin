package org.jenkinsci.plugins.sonargerrit.data.entity;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */

public class Rule {

    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String key;

    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String rule;

    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String repository;

    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
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
