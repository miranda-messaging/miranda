package com.ltsllc.miranda;

/**
 * Something Very Bad happend startup.  This is usually fatal.
 */
public class StartupPanic extends Panic {
    public enum StartupReasons {
        CreatingSelector,
        CreatingSslContext, // we could not create an SSL context for the NetworkListener
        KeystoreDoesNotExist,
        ExceptionCreatingHttpServer,
        ExceptionInStart, // the start method of the current state threw an exception
        ExceptionManipulatingKeystore, // An exception was caught while trying to manipulate the keystore
        ExceptionOpeningKeystore, // an exception was caught trying to open the system Keystore
        ExceptionScanning,
        ExceptionStartingHttpServer,
        ExceptionStartingNetwork, // an exception was thrown while trying to start the network
        ExceptionStartingNetworkListener,
        ExceptionListening,
        ExceptionLoadingProperties,
        ExceptionWatchingFile,
        MissingKey, // A key that the system needs is missing from the keystore
        MissingProperty, // A Property that the system requires to start up is missing
        NetworkListener, // NetworkListener.startup threw an exception
        NullStartState, // The currentState was null for a Consumer when starting
        SendingMessage, // An exception occurred while trying to sendToMe a message
        StartupFailed, // Some sort of error prevented the Startup class from completing
        UncheckedException, // an unchecked exception was thrown during startup
        UndefinedKeystore, // The keystore property was null or empty
        UnrecognizedEncryptionMode,
        WebServer // a problem that prevented the web server from starting occurred
    }

    private StartupReasons startupReason;

    public StartupReasons getStartupReason() {
        return startupReason;
    }

    public StartupPanic (String message, Throwable cause, StartupReasons reason) {
        super (message, cause, Reasons.Startup);

        this.startupReason = reason;
    }

    public StartupPanic (String message, StartupReasons reason) {
        super (message, Reasons.Startup);

        this.startupReason = reason;
    }
}
