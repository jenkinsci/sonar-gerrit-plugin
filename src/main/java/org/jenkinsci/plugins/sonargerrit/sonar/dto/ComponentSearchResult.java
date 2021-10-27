package org.jenkinsci.plugins.sonargerrit.sonar.dto;

import java.util.List;

public class ComponentSearchResult {
    private Paging paging;
    private List<Component> components;

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }
}
