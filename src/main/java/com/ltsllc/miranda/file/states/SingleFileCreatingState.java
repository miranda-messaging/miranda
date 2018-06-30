package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.writer.WriteResponseMessage;

public class SingleFileCreatingState extends State {
    private State readyState;

    public State getReadyState() {
        return readyState;
    }

    public void setReadyState(State readyState) {
        this.readyState = readyState;
    }

    public SingleFileCreatingState (State readyState, SingleFile singleFile) {
        super(singleFile);
        setReadyState(readyState);
    }

    public State processMessage (Message message) {
        State nextState = null;
        switch (message.getSubject()) {
            case WriteResponse: {
                WriteResponseMessage writeResponseMessage = (WriteResponseMessage) message;
                nextState = processWriteResponseMessage(writeResponseMessage);
                break;
            }

            default: {
                defer(message);
                break;
            }
        }

        return nextState;
    }

    public State processWriteResponseMessage (WriteResponseMessage writeResponseMessage) {
        if (writeResponseMessage.getResult() == Results.Success) {
            restoreDeferredMessages();
            return getReadyState();
        } else {
            Panic panic = new Panic("Exception writing file", writeResponseMessage.getException(),
                    Panic.Reasons.ExceptionWritingFile);
            Miranda.panicMiranda(panic);
            return this;
        }
    }
}
