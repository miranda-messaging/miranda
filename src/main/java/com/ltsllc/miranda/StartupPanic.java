package com.ltsllc.miranda;

/**
 * Something Very Bad happend startup.  This is usually fatal.
 */
public class StartupPanic extends Panic {
    public enum StartupReasons {
        CreatingSelector,
        CreatingSslContext, // we could not create an SSL context for the NetworkListener
        ExceptionCreatingHttpServer,
        ExceptionStartingHttpServer,
        ExceptionListening,
        NetworkListener, // NetworkListener.startup threw an exception
        SendingMessage, // An exception occurred while trying to sendToMe a message
        StartupFailed, // Some sort of error prevented the Startup class from completing
        UncheckedException, // an unchecked exception was thrown during startup
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
}
