package com.ltsllc.miranda.user;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;

/**
 * Created by Clark on 3/31/2017.
 */
public class UnknownUserException extends MirandaException {
    private String name;

    public String getName() {
        return name;
    }

    public UnknownUserException (String name) {
        this.name = name;
    }
}
