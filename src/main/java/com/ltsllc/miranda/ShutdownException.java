package com.ltsllc.miranda;

/**
 * Created by Clark on 6/1/2017.
 */
public class ShutdownException extends RuntimeException {
    public ShutdownException(String message) {
        super(message);
    }

    public ShutdownException(Throwable throwable) {
        super(throwable);
    }

    public ShutdownException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
