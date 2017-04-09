package com.ltsllc.miranda.user;

import com.ltsllc.miranda.MirandaException;

/**
 * Created by Clark on 4/7/2017.
 */
public class DuplicateUserException extends MirandaException {
    public DuplicateUserException (String message) {
        super(message);
    }
}
