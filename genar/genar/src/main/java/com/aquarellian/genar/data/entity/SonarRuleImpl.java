package com.aquarellian.genar.data.entity;

import com.aquarellian.genar.data.entity.interfaces.SonarRule;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 15:43
 * <p/>
 * $Id$
 */
public class SonarRuleImpl implements SonarRule{
    private String key;
    private String rule;
    private String repository;
    private String name;

    public SonarRuleImpl(String key, String rule, String repository, String name) {
        this.key = key;
        this.rule = rule;
        this.repository = repository;
        this.name = name;
    }

    public SonarRuleImpl() {
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

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SonarRule rule1 = (SonarRule) o;

        if (key != null ? !key.equals(rule1.getKey()) : rule1.getKey() != null)
            return false;
        if (name != null ? !name.equals(rule1.getName()) : rule1.getName() != null)
            return false;
        if (repository != null ? !repository.equals(rule1.getRepository()) : rule1.getRepository() != null)
            return false;
        if (rule != null ? !rule.equals(rule1.getRule()) : rule1.getRule() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        result = 31 * result + (repository != null ? repository.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
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
