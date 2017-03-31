package com.ltsllc.miranda.property;

import com.ltsllc.miranda.MirandaException;

/**
 * Created by Clark on 3/30/2017.
 */
public class InvalidPropertyException extends MirandaException {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public InvalidPropertyException(Throwable cause, String name, String value) {
        super("Invalid property", cause);
        this.name = name;
        this.value = value;
    }
}
