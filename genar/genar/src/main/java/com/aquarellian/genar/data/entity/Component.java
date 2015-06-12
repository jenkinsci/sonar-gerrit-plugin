package com.aquarellian.genar.data.entity;


/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 15:43
 * <p/>
 * $Id$
 */
public class Component {
    private String key;

    private String path;

    private String moduleKey;

    private String status;

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public String getModuleKey() {
        return moduleKey;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) { //NOSONAR
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Component component = (Component) o;

        if (key != null ? !key.equals(component.key) : component.key != null) {
            return false;
        }
        if (path != null ? !path.equals(component.path) : component.path != null) {
            return false;
        }
        if (moduleKey != null ? !moduleKey.equals(component.moduleKey) : component.moduleKey != null) {
            return false;
        }

        if (status != null ? !status.equals(component.status) : component.status != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (moduleKey != null ? moduleKey.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
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
