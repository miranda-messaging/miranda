package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ClusterFileMessage;
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
 * Created by Clark on 1/29/2017.
 */
public class JoiningState extends NodeState {
    private Logger logger = Logger.getLogger(JoiningState.class);

    private Cluster cluster;


    public Cluster getCluster() {
        return cluster;
    }

    public JoiningState (Node node, Cluster cluster, Network network) {
        super (node, network);

        this.cluster = cluster;
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


            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) m;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) m;
                nextState = processVersionMessage(versionMessage);
                break;
            }

            case ClusterFile: {
                ClusterFileMessage clusterFileMessage = (ClusterFileMessage) m;
                nextState = processClusterFileMessage (clusterFileMessage);
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

            default:
                logger.fatal(this + " does not understand network message " + networkMessage.getWireMessage().getWireSubject());
                System.exit(1);
        }

        return nextState;
    }


    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage("cluster");
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        return this;
    }


    private State processGetVersionsWireMessage (GetVersionsWireMessage getVersionsWireMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getNode().getQueue(), this, getNode().getQueue());
        send (Miranda.getInstance().getQueue(), getVersionMessage);

        return this;
    }


    private State processVersionMessage (VersionMessage versionMessage) {
        List<NameVersion> versions = new ArrayList<NameVersion>();

        versions.add(versionMessage.getNameVersion());

        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(versions);
        sendOnWire(versionsWireMessage);

        return this;
    }

    private State processClusterFileMessage (ClusterFileMessage clusterFileMessage) {
        ClusterFileWireMessage clusterFileWireMessage = new ClusterFileWireMessage(clusterFileMessage.getFile(), clusterFileMessage.getVersion());
        sendOnWire(clusterFileWireMessage);

        return this;
    }

    private State processJoinResponse (JoinResponseWireMessage joinResponse) {
        State nextState = this;

        if (joinResponse.getResult() == JoinResponseWireMessage.Responses.Success) {
            logger.info ("Successfully joined cluster");

            getCluster().sendNewNode(getNode().getQueue(), this, getNode());

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
