package com.ltsllc.miranda.panics;

import com.ltsllc.miranda.panics.Panic;

/**
 * A Panic that represents an Exception being thrown during the course of handling a Message.
 * <p>
 * <p>
 * An instance of this class must have a non-null cause.
 * An instance should have a non-null reason.
 * </p>
 */
public class ExceptionPanic extends Panic {
    public ExceptionPanic(String message, Throwable cause, Reasons reason) {
        super(message, cause, reason);
    }

    public ExceptionPanic(Throwable exception, Reasons reason) {
        super(exception, reason);
    }
}
