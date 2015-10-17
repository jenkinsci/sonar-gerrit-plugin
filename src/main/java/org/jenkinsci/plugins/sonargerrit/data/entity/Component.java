package org.jenkinsci.plugins.sonargerrit.data.entity;


import javax.annotation.Nullable;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */

public class Component {
    @SuppressWarnings(value="unused")
    private String key;

    @SuppressWarnings(value="unused")
    private String path;

    @SuppressWarnings(value="unused")
    private String moduleKey;

    @SuppressWarnings(value="unused")
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
