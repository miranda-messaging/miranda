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
        CouldNotWrite, // A subsystem received a WriteFailed message
        DoesNotUnderstand, // A state received a message it does not know how to process
        DoesNotUnderstandNetworkMessage, // A state received a network message it does not know how to process
        ExceptionCreatingSslContext,
        ExceptionDuringNetworkSend, // an InterruptedException was thrown while waiting for a network to complete
        ExceptionDuringNewConnection,
        ExceptionGettingNextMessage, // an InterruptedException was thrown while waiting for the next message
        ExceptionInProcessMessage, // an unchecked exception was thrown in processMessage
        ExceptionInRun, // an unchecked exception was thrown in a Consumers run method
        ExceptionLoadingProperties, // an exception was thrown while trying to load the properties file
        ExceptionReceivingMessage,
        ExceptionSendingMessage,
        ExceptionTryingToCalculateVersion, // an exception was thrown while calculating a new version.
        ExceptionTryingToRectify,
        ExceptionWaitingForNextConnection, // an exception was thrown while waiting for a new node
        ExceptionWritingFile,
        NetworkThreadCrashed, // one of our network connections died
        Network, // We cannot communicate with anyone
        NullCurrentState, // The currentState is null for a consumer
        Startup, // something happend during startup this usually means we are an instance of StartupPanic
        ServletTimeout, // A servlet timed out waiting for a response from the system
        Test,
        UncheckedException, // an unchecked exception was thrown
        UnrecognizedNode // a node shut down that we don't have a record of
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

    public Panic (String message, Reasons reason) {
        super (message);

        this.reason = reason;
    }
}
