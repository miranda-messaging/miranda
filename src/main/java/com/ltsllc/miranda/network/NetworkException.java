package com.ltsllc.miranda.network;

/**
 * Created by Clark on 3/1/2017.
 */
public class NetworkException extends Exception {
    public enum Errors {
        ClassNotFound,
        ErrorGettingStreams,
        ExceptionSending,
        ExceptionConnecting,
        ExceptionCreating,
        ExceptionReceiving,
        InterruptedSending,
        Test,
        UnrecognizedHandle
    }

    private Errors error;

    public Errors getError() {
        return error;
    }

    public NetworkException (Errors error) {
        this.error = error;
    }


    public NetworkException (String string, Throwable throwable, Errors error) {
        super(string, throwable);

        this.error = error;
    }

    public NetworkException (Throwable throwable, Errors error) {
        super(throwable);

        this.error = error;
    }

    public NetworkException (String string, Errors error) {
        super(string);

        this.error = error;
    }
}
