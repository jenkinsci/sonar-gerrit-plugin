package com.aquarellian.genar.data.entity;

import com.aquarellian.genar.data.entity.interfaces.SonarComponent;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 15:43
 * <p/>
 * $Id$
 */
public class SonarComponentImpl implements SonarComponent{
    private  String key;

    private  String path;

    public SonarComponentImpl(String key, String path) {
        this.key = key;
        this.path = path;
    }

    public SonarComponentImpl() {
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) { //NOSONAR
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SonarComponentImpl component = (SonarComponentImpl) o;

        if (key != null ? !key.equals(component.key) : component.key != null)
            return false;
        if (path != null ? !path.equals(component.path) : component.path != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Component{" +
                "key='" + key + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
