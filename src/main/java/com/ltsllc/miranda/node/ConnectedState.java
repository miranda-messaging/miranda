package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 1/22/2017.
 */
public class ConnectedState extends NodeState {
    public ConnectedState (Node node) {
        super(node);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage (networkMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    public State processNetworkMessage (NetworkMessage networkMessage) {
        State nextState = this;

        JoinSuccessWireMessage joinSuccessWireMessage = new JoinSuccessWireMessage();
        sendOnWire (joinSuccessWireMessage);

        return nextState;
    }
}
