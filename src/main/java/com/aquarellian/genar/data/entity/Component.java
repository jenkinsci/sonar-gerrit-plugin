package com.aquarellian.genar.data.entity;


import javax.annotation.Nullable;

/**
 * @author Tatiana Didik (aquarellian@gmail.com)
 */

public class Component {
    private String key;

    private String path;

    private String moduleKey;

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
