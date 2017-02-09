package com.ltsllc.miranda.node;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.ClusterFileMessage;
import com.ltsllc.miranda.cluster.VersionsMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class NodeReadyState extends NodeState {
    private Map<String, Version> versions = new HashMap<String, Version>();


    public NodeReadyState(Node node) {
        super(node);
    }

    public Map<String, Version> getVersions() {
        return versions;
    }

    @Override
    public State processNetworkMessage(NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case GetVersions: {
                nextState = processGetVersionsWireMessage(networkMessage.getWireMessage());
                break;
            }

            case Versions: {
                VersionsWireMessage versionsWireMessage = (VersionsWireMessage) networkMessage.getWireMessage();
                nextState = processVersionsWireMessage(versionsWireMessage);
                break;
            }


            case ClusterFile: {
                ClusterFileWireMessage clusterFileWireMessage = (ClusterFileWireMessage) networkMessage.getWireMessage();
                nextState = processClusterFileWireMessage (clusterFileWireMessage);
                break;
            }

            default: {
                nextState = super.processNetworkMessage(networkMessage);
                break;
            }
        }

        return nextState;
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

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage (versionMessage);
                break;
            }

            case ConnectionClosed: {
                nextState = StopState.getInstance();
                break;
            }

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) message;
                nextState = processGetClusterFileMessage(getClusterFileMessage);
                break;
            }

            default :
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processVersionMessage (VersionMessage versionMessage) {
        getVersions().put(versionMessage.getNameVersion().getName(), versionMessage.getNameVersion().getVersion());


        if (versions.size() >= 1) {
            VersionsWireMessage versionsWireMessage = new VersionsWireMessage(versionsToList());
            sendOnWire(versionsWireMessage);
        }

        return this;
    }


    private State processGetVersionsWireMessage (WireMessage wireMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getNode().getQueue(), this);
        send(Cluster.getInstance().getQueue(), getVersionMessage);

        return this;
    }


    private List<NameVersion> versionsToList () {
        List<NameVersion> list = new ArrayList<NameVersion>();

        NameVersion nameVersion = new NameVersion("cluster", versions.get("cluster"));
        list.add(nameVersion);

        return list;
    }


    public State processVersionsWireMessage (VersionsWireMessage versionsWireMessage) {
        VersionsMessage versionsMessage = new VersionsMessage(getNode().getQueue(), this, versionsWireMessage.getVersions());

        Consumer.staticSend (versionsMessage, Cluster.getInstance().getQueue());

        return this;
    }

    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        State nextState = this;

        GetClusterFileWireMessage getClusterFileWireMessage = new GetClusterFileWireMessage();
        sendOnWire(getClusterFileWireMessage);

        return nextState;
    }


    private State processClusterFileWireMessage (ClusterFileWireMessage clusterFileWireMessage) {
        ClusterFileMessage clusterFileMessage = new ClusterFileMessage(getNode().getQueue(), this, clusterFileWireMessage.getContentAsBytes(), clusterFileWireMessage.getVersion());
        send (Cluster.getInstance().getQueue(), clusterFileMessage);

        return this;
    }
}
