package com.ltsllc.miranda.operations.bootstrap.states;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.operations.bootstrap.BootstrapOperation;
import com.ltsllc.miranda.servlet.bootstrap.BootstrapMessage;

public class Start extends State {
    private BootstrapOperation operation;

    public BootstrapOperation getOperation() {
        return operation;
    }

    public Start (BootstrapOperation bootstrapOperation) {
        operation = bootstrapOperation;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case Bootstrap: {
                BootstrapMessage bootstrapMessage = (BootstrapMessage) message;
                nextState = processBootstrapMessage(bootstrapMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processBootstrapMessage(BootstrapMessage bootstrapMessage) {
        return new Processing(getOperation(), bootstrapMessage);
    }
}
