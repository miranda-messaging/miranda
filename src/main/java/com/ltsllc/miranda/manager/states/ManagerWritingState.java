package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.manager.Manager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.writer.WriteResponseMessage;


public class ManagerWritingState extends ManagerState {
    private State readyState;

    public State getReadyState() {
        return readyState;
    }

    public void setReadyState(State readyState) {
        this.readyState = readyState;
    }

    public Manager getManager() {
        return (Manager) getContainer();
    }

    public ManagerWritingState(State readyState, Manager manager) {
        super(manager);
        setReadyState(readyState);
    }

    public State processMessage(Message message) {
        State nextState = this;

        try {
            switch (message.getSubject()) {
                case GarbageCollection: {
                    GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                    nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                    break;
                }

                case WriteResponse: {
                    WriteResponseMessage writeResponseMessage = (WriteResponseMessage) message;
                    nextState = processWriteResponseMessage (writeResponseMessage);
                    break;
                }

                default: {
                    nextState = super.processMessage(message);
                    break;
                }
            }

            return nextState;
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
        }

        return nextState;
    }

    public State processWriteResponseMessage (WriteResponseMessage writeResponseMessage) {
        if (writeResponseMessage.getResult() == Results.Success) {
            return getReadyState();
        } else {
            Panic panic = new Panic("Exception writing file", writeResponseMessage.getException(), Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return this;
        }
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        return this;
    }
}
