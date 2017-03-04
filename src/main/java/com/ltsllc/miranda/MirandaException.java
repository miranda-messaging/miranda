package com.ltsllc.miranda;

/**
 * This class distinguishes something we threw for something somebody else threw.
 */
public class MirandaException extends Exception {
    public MirandaException (String message) {
        super(message);
    }

    public MirandaException (String message, Throwable cause) {
        super (message, cause);
    }
}
