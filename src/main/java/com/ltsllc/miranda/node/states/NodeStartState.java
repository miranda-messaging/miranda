package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.JoinResponseWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import org.apache.log4j.Logger;

/**
 * A new node enters this state until it delivers a join message to the
 * remote system.
 */
public class NodeStartState extends State {
    private static Logger logger = Logger.getLogger(NodeStartState.class);

    private Node node;
    private Network network;
    private Cluster cluster;

    public NodeStartState (Node node, Network network) {
        super(node);
        this.node = node;
        this.network = network;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public Network getNetwork() {
        return network;
    }

    public Node getNode() {

        return node;
    }

    public State processMessage (Message m) {
        State nextState = null;

        switch (m.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) m;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            default :
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }


    private State processNetworkMessage (NetworkMessage networkMessage) {
        State nextState = this;

        switch(networkMessage.getWireMessage().getWireSubject()) {
            case JoinResponse: {
                JoinResponseWireMessage joinResponseWireMessage = (JoinResponseWireMessage) networkMessage.getWireMessage();
                nextState = processJoinResponse (joinResponseWireMessage);
                break;
            }

            default: {
                logger.error("Received unexpected network message, ignoring " + networkMessage.getWireMessage().getWireSubject());
                break;
            }
        }

        return nextState;
    }

    private State processJoinResponse (JoinResponseWireMessage joinResponse) {
        State nextState = this;

        if (joinResponse.getResult() == JoinResponseWireMessage.Responses.Success)
        {
            logger.info ("Joined with remote cluster");
            NodeReadyState nodeReadyState = new NodeReadyState(getNode(), getNetwork());
            nextState = nodeReadyState;

            getCluster().sendNewNode(getNode().getQueue(), this, getNode());
        }
        else
        {
            logger.warn ("Failed to join with remote cluster, closing connection");

            getNetwork().sendClose(getNode().getQueue(), this, getNode().getHandle());

            ClosingState closingState = new ClosingState(getNode(), getNetwork());
            nextState = closingState;
        }

        return nextState;
    }
}
