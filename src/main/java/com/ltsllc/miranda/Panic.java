package com.ltsllc.miranda;

/**
 * A request to shutdown.
 *
 * <P>
 *     Use of this class means that something Very Bad happend and that some
 *     part of the system thinks that we should stop.
 * </P>
 */
public class Panic extends Exception {
    public enum Reasons {
        DoesNotUnderstand, // A state received a message it does not know how to process
        ExceptionCreatingSslContext,
        ExceptionGettingNextMessage, // an InterruptedException was thrown while waiting for the next message
        ExceptionSendingMessage,
        ExceptionDuringNetworkSend, // an InterruptedException was thrown while waiting for a network to complete
        ExceptionDuringNewConnection,
        ExceptionLoadingProperties, // an exception was thrown while trying to load the properties file
        ExceptionWaitingForNextConnection, // an exception was thrown while waiting for a new node
        ExceptionReceivingMessage,
        ExceptionStartingServletHandler,
        NetworkThreadCrashed, // one of our network connections died
        Network, // We cannot communicate with anyone
        Startup, // something happend during startup this usually means we are an instance of StartupPanic
        Select,
        ServletTimeout, // A servlet timed out waiting for a response from the system
        UncheckedException // an unchecked exception was thrown
    }

    private Reasons reason;

    public Reasons getReason() {
        return reason;
    }

    public Panic (String message, Throwable cause, Reasons reason) {
        super(message, cause);

        this.reason = reason;
    }

    public Panic (Throwable cause, Reasons reason) {
        super(cause);

        this.reason = reason;
    }
}
