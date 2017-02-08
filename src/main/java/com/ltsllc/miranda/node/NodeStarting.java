package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.ConnectedMessage;

/**
 * Created by Clark on 1/22/2017.
 */
public class NodeStarting extends NodeState {
    public NodeStarting (Node node) {
        super(node);
    }

    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case Connected:
            {
                ConnectedMessage connectedMessage = (ConnectedMessage) m;
                processConnectedMessage(connectedMessage);
                break;
            }
            
            default: {
                super.processMessage(m);
                break;
            }
        }

        return nextState;
    }

    private State processConnectedMessage (ConnectedMessage connectedMessage)
    {
        getNode().setChannel(connectedMessage.getChannel());

        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        SyncingState syncingState = new SyncingState(getNode());

        return syncingState;
    }
}
