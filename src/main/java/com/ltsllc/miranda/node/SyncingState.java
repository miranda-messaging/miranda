package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.Cluster;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/6/2017.
 */

/**
 * Represents when a node has joined the cluster and wants to ensure that its
 * files are up to date.
 */
public class SyncingState extends NodeState {
    private static Logger logger = Logger.getLogger(SyncingState.class);

    private List<NameVersion> versions = new ArrayList<NameVersion>();

    public SyncingState (Node node) {
        super(node);
    }

    public List<NameVersion> getVersions() {
        return versions;
    }

    @Override
    public State start() {
        GetVersionsWireMessage versionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(versionsWireMessage);
        return this;
    }


    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage(versionMessage);
                break;
            }

            case ConnectionClosed: {
                nextState = StopState.getInstance();
                break;
            }

            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
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

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case Versions: {
                VersionsWireMessage versionsWireMessage = (VersionsWireMessage) networkMessage.getWireMessage();
                nextState = processVersionsMessage(versionsWireMessage);
                break;
            }

            case GetVersions: {
                GetVersionsWireMessage getVersionWireMessage = (GetVersionsWireMessage) networkMessage.getWireMessage();
                nextState = processGetVersionWireMessage(getVersionWireMessage);
                break;
            }

            case Join: {
                JoinWireMessage joinWireMessage = (JoinWireMessage) networkMessage.getWireMessage();
                nextState = processJoinWireMessage(joinWireMessage);
                break;
            }

            default: {
                nextState = super.processNetworkMessage(networkMessage);
                break;
            }
        }

        return nextState;
    }


    private State processGetVersionWireMessage (GetVersionsWireMessage getVersionsWireMessage) {
        State nextState = this;

        GetVersionMessage getVersionMessage = new GetVersionMessage(getNode().getQueue(), this);
        send(Cluster.getInstance().getQueue(), getVersionMessage);

        return nextState;
    }


    private State processVersionsMessage (VersionsWireMessage versionsWireMessage) {
        return new NodeReadyState(getNode());
    }


    private State processVersionMessage (VersionMessage versionMessage) {
            getVersions().add(versionMessage.getNameVersion());

        if (getVersions().size() >= 1) {
            VersionsWireMessage versionsWireMessage = new VersionsWireMessage(getVersions());
            sendOnWire(versionsWireMessage);
        }

        return this;
    }


    private State processJoinWireMessage (JoinWireMessage joinWireMessage) {
        getNode().setDns(joinWireMessage.getDns());
        getNode().setIp(joinWireMessage.getIp());
        getNode().setPort(joinWireMessage.getPort());
        getNode().setDescription(joinWireMessage.getDescription());

        NodeUpdatedMessage nodeUpdatedMessage = new NodeUpdatedMessage(getNode().getQueue(), this, getNode());
        send(Cluster.getInstance().getQueue(), nodeUpdatedMessage);

        NodeReadyState nodeReadyState = new NodeReadyState(getNode());
        return nodeReadyState;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        getNode().sendOnWire(getVersionsWireMessage);

        return this;
    }
}
