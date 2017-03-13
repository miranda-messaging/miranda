package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.JoinResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import org.apache.log4j.Logger;

/**
 * A new node enters this state when it is trying to connect to a remote
 * system.  The state assumes that the network has been asked to connect to
 * the remote host.
 */
public class NodeStartState extends NodeState {
    public NodeStartState (Node node, Network network) {
        super(node, network);
    }

    public State processMessage (Message m) {
        State nextState = null;

        switch (m.getSubject()) {
            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) m;
                nextState = processConnectMessage(connectMessage);
                break;
            }

            default :
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }


    private State processConnectMessage (ConnectMessage connectMessage) {
        getNode().connect();

        ConnectingState connectingState = new ConnectingState(getNode(), getNetwork());
        return connectingState;
    }
}
