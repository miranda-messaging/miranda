/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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
        EncryptException, // trying to encrypt a file prior to writing failed.  We effectively cannot write anything
        ErrorLoadingFile,
        ExceptionCalculatingSha1,
        ExceptionCreatingSslContext,
        ExceptionDecrypting,
        ExceptionDuringNetworkSend, // an InterruptedException was thrown while waiting for a network to complete
        ExceptionDuringNewConnection,
        ExceptionEncrypting,
        ExceptionGettingNextMessage, // an InterruptedException was thrown while waiting for the next message
        ExceptionInProcessMessage, // an unchecked exception was thrown in processMessage
        ExceptionInRun, // an unchecked exception was thrown in a Consumers run method
        ExceptionLoadingFile,
        ExceptionLoadingProperties, // an exception was thrown while trying to load the properties file
        ExceptionPaesingJson,
        ExceptionReceivingMessage,
        ExceptionReadingFile,
        ExceptionSendingMessage,
        ExceptionStartingServlets,
        ExceptionTryingToCalculateVersion, // an exception was thrown while calculating a new version.
        ExceptionTryingToRectify,
        ExceptionWaitingForNextConnection, // an exception was thrown while waiting for a new node
        ExceptionWritingFile,
        NetworkThreadCrashed, // one of our network connections died
        Network, // We cannot communicate with anyone
        NullCurrentState, // The currentState is null for a consumer
        OutOfMemory,
        Startup, // something happend during startup this usually means we are an instance of StartupPanic
        ServletTimeout, // A servlet timed out waiting for a response from the system
        Test,
        UncheckedException, // an unchecked exception was thrown
        UnknownUser, // we were told to update a user that doesn't exist
        UnrecognizedNode // a node shut down that we don't have a record of
    }

    private Reasons reason;
    private String additionalInfo;

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public Reasons getReason() {
        return reason;
    }

    public Panic (String message, Throwable cause, Reasons reason) {
        super(message, cause);
        basicConstructor(reason, cause);
    }

    public Panic (Throwable cause, Reasons reason) {
        super(cause);
        basicConstructor(reason, cause);
    }

    public Panic (String message, Reasons reason) {
        super (message);
        basicConstructor(reason, null);
    }

    public Panic (String message, Reasons reason, String additionalInfo) {
        super(message);

        this.reason = reason;
        this.additionalInfo = additionalInfo;
    }

    public void basicConstructor (Reasons reason, Throwable throwable) {
        this.reason = reason;

        if (throwable instanceof OutOfMemoryError) {
            reason = Reasons.OutOfMemory;
        }

        if (throwable != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            printWriter.close();
            try {
                stringWriter.close();
            } catch (IOException e) {}
            this.additionalInfo = stringWriter.toString();
        }
    }
}
