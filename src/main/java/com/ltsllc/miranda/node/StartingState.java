package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.ConnectMessage;
import com.ltsllc.miranda.network.ConnectToMessage;

/**
 * Created by Clark on 1/29/2017.
 */

/**
 * Represents when a node is starting
 */
public class StartingState extends NodeState {
    public StartingState(Node node) {
        super(node);
    }

    public State processMessage(Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) m;
                nextState = processConnectMessage(connectMessage);
                break;
            }


            default: {
                nextState = super.processMessage(m);
                break;
            }
        }

        return nextState;
    }

    private State processConnectMessage(ConnectMessage connectMessage) {
        State nextState = new ConnectingState(getNode());
        ConnectToMessage connectTo = new ConnectToMessage(getNode().getDns(), getNode().getPort(), nextState.getContainer().getQueue(), this);
        send(getNode().getNetwork(), connectTo);
        return nextState;
    }
}
