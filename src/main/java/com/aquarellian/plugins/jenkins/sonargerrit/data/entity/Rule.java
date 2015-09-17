package com.aquarellian.plugins.jenkins.sonargerrit.data.entity;



/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */

public class Rule {
    private String key;
    private String rule;
    private String repository;
    private String name;

    public Rule(String key, String rule, String repository, String name) {
        this.key = key;
        this.rule = rule;
        this.repository = repository;
        this.name = name;
    }

    public Rule() {
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setName(String name) {
        this.name = name;
    }

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
