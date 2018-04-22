package com.ltsllc.commons.commadline;

/**
 * Created by miranda on 7/11/2017.
 */
public class CommandLineException extends Exception {
    public CommandLineException(Throwable cause) {
        super(cause);
    }

    public CommandLineException(String message) {
        super(message);
    }
}
