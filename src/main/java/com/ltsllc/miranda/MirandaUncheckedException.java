package com.ltsllc.miranda;

public class MirandaUncheckedException extends RuntimeException {
    public MirandaUncheckedException(Throwable t) {
        super(t);
    }

    public MirandaUncheckedException(String message) {
        super(message);
    }

    public MirandaUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }
}
