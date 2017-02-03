package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.Connected;

import java.util.concurrent.BlockingQueue;

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
                Connected connected = (Connected) m;
                processConnected(connected);
                break;
            }
            
            default: {
                super.processMessage(m);
                break;
            }
        }

        return nextState;
    }

    private State processConnected (Connected connected)
    {
        getNode().setChannel(connected.getChannel());

        String stringMessage = "hello";
        byte[] message = stringMessage.getBytes();
        getNode().getChannel().writeAndFlush(message);

        return this;
    }
}
