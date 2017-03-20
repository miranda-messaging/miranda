package com.ltsllc.miranda.servlet;

/**
 * Created by Clark on 3/4/2017.
 */
public class Property {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Property (String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean equals (Object o) {
        if (null == o || !(o instanceof Property))
            return false;

        Property other = (Property) o;

        return getName().equals(other.getName()) && getValue().equals(other.getValue());
    }
}
