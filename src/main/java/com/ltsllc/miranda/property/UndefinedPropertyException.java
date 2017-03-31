package com.ltsllc.miranda.property;

import com.ltsllc.miranda.MirandaException;

/**
 * Created by Clark on 3/30/2017.
 */
public class UndefinedPropertyException extends MirandaException {
    private String name;

    public String getName() {
        return name;
    }

    public UndefinedPropertyException (String name) {
        this.name = name;
    }
}
