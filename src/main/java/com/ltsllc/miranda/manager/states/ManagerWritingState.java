package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.file.messages.WriteFileResponseMessage;
import com.ltsllc.miranda.manager.Manager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;


public class ManagerWritingState extends ManagerState {
    public Manager getManager() {
        return (Manager) getContainer();
    }

    public ManagerWritingState(Manager manager) {
        super(manager);
    }

    public State processMessage(Message message) {
        State nextState = this;

        try {
            switch (message.getSubject()) {
                case WriteFileResponse: {
                    WriteFileResponseMessage writeFileResponseMessage = (WriteFileResponseMessage) message;
                    nextState = processWriteFileResponseMessage(writeFileResponseMessage);
                    break;
                }

                case GarbageCollection: {
                    GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                    nextState = processGarbageCollectionMessage(garbageCollectionMessage);
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

    public State processWriteFileResponseMessage(WriteFileResponseMessage writeFileResponseMessage) throws MirandaException {
        if (writeFileResponseMessage.getResult() == Results.Success) {
            return getManager().getReadyState();
        } else {
            Panic panic = new Panic("Failure writing file", writeFileResponseMessage.getWhere(),
                    Panic.Reasons.FailedWritigFile);
            Miranda.panicMiranda(panic);
        }

        return this;
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        return this;
    }
}
