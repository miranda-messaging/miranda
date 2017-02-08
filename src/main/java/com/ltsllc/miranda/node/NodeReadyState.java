package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.Cluster;

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


    public State processVersionsWireMessage (WireMessage wireMessage) {

        GetVersionMessage getVersionMessage = new GetVersionMessage(getNode().getQueue(), this);

        Consumer.staticSend (getVersionMessage, Cluster.getInstance().getQueue());

        return this;
    }
}
