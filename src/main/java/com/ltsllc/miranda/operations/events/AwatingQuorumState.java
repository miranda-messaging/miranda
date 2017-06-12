package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.operations.Operation;

/**
 * Created by clarkhobbie on 6/12/17.
 */
abstract public class AwatingQuorumState extends State {
    abstract boolean requiresWrites ();

    public static final String NAME = "awaiting quorum state";

    public AwatingQuorumState (Operation operation) {
        super(operation);
    }

    private Operation getOperation () {
        return (Operation) getContainer();
    }

    public State processMessage (Message message) {
        State nextState = getOperation().getCurrentState();

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public String toString () {
        return NAME;
    }
}
