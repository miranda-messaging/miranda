package com.ltsllc.miranda.property;

import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Updateable;

/**
 * Created by Clark on 4/14/2017.
 */
public class Property implements Updateable<Property>, Matchable<Property> {
    private String name;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateFrom (Property other) {
        setValue(other.getValue());
    }

    public boolean matches (Property other) {
        return getName().equals(other.getName());
    }
}
