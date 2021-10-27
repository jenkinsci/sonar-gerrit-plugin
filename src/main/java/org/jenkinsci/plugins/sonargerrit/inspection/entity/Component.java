package org.jenkinsci.plugins.sonargerrit.inspection.entity;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nullable;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */

public class Component {
    @SuppressWarnings("unused")
    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")

    private String key;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String moduleKey;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String path;

    @SuppressWarnings("unused")
    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String status;

    public String getKey() {
        return key;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    @Nullable
    public String getModuleKey() {
        return moduleKey;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Component{" +
                "key='" + key + '\'' +
                ", path='" + path + '\'' +
                ", moduleKey='" + moduleKey + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
