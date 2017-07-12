package org.jenkinsci.plugins.sonargerrit.data.entity;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nullable;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 */

public class Component {
    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")

    private String key;

    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String moduleKey;

    @SuppressFBWarnings ("UWF_UNWRITTEN_FIELD")
    private String path;

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
