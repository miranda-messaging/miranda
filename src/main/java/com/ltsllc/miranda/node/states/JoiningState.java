package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ClusterFileMessage;
import com.ltsllc.miranda.miranda.GetVersionsMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.node.networkMessages.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A node which has sent a join message enters this state and remains here
 * until it receives a reply.
 */
public class JoiningState extends NodeState {
    private Logger logger = Logger.getLogger(JoiningState.class);


    public JoiningState (Node node, Network network) {
        super (node, network);
    }

    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) m;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) m;
                nextState = processGetClusterFileMessage (getClusterFileMessage);
                break;
            }

            case GetVersions: {
                GetVersionsMessage getVersionsMessage = (GetVersionsMessage) m;
                nextState = processGetVersionsMessage(getVersionsMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }

        }

        return nextState;
    }

    public State processNetworkMessage (NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case JoinResponse:{
                JoinResponseWireMessage joinResponseWireMessage = (JoinResponseWireMessage) networkMessage.getWireMessage();
                nextState = processJoinResponse(joinResponseWireMessage);
                break;
            }

            case GetVersions: {
                GetVersionsWireMessage getVersionsWireMessage = (GetVersionsWireMessage) networkMessage.getWireMessage();
                nextState = processGetVersionsWireMessage(getVersionsWireMessage);
                break;
            }

            default: {
                nextState = super.processNetworkMessage(networkMessage);
                break;
            }
        }

        return nextState;
    }


    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage(Cluster.FILE_NAME);
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetVersionsMessage (GetVersionsMessage getVersionsMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        return this;
    }


    private State processGetVersionsWireMessage (GetVersionsWireMessage getVersionsWireMessage) {
        GetVersionsMessage getVersionsMessage = new GetVersionsMessage(getNode().getQueue(), this);
        send (Miranda.getInstance().getQueue(), getVersionsMessage);

        return this;
    }

    private State processJoinResponse (JoinResponseWireMessage joinResponse) {
        State nextState = this;

        if (joinResponse.getResult() == JoinResponseWireMessage.Responses.Success) {
            logger.info ("Successfully joined cluster");

            NodeReadyState nodeReadyState = new NodeReadyState(getNode(), getNetwork());
            nextState = nodeReadyState;
        }
        else
        {
            logger.warn ("Failed to join cluster, closing connection");

            getNetwork().sendClose(getNode().getQueue(), this, getNode().getHandle());

            ClosingState closingState = new ClosingState(getNode(), getNetwork());
            nextState = closingState;
        }

        return nextState;
    }
}
