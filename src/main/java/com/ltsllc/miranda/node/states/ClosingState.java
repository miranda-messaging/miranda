package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.network.messages.ClosedMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;

/**
 * A node is waiting for a connection to close.
 */
public class ClosingState extends NodeState {
    public ClosingState (Node node, Network network) {
        super(node, network);
    }

    public State processMessage (Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Closed: {
                ClosedMessage closedMessage = (ClosedMessage) message;
                nextState = processClosedMessage (closedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processClosedMessage (ClosedMessage closedMessage) {
        return StopState.getInstance();
    }
}
