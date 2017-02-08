package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.NodeAddedMessage;
import com.ltsllc.miranda.node.GetVersionMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.VersionMessage;

/**
 * Created by Clark on 2/6/2017.
 */
public class ClusterFileReadyState extends State {
    private ClusterFile clusterFile;

    public ClusterFileReadyState(Consumer consumer, ClusterFile clusterFile) {
        super(consumer);
        this.clusterFile = clusterFile;
    }

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case NodeAdded: {
                NodeAddedMessage nodeAddedMessage = (NodeAddedMessage) message;
                nextState = processNodeAddedMessage(nodeAddedMessage);
                break;
            }

            case NewNode: {
                NewNodeMessage newNodeMessage = (NewNodeMessage) message;
                nextState = processNewNodeMessag(newNodeMessage);
                break;
            }

            case WriteSucceeded: {
                break;
            }

            case Version: {

            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("cluster", getClusterFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getClusterFile().getQueue(), this, nameVersion);
        send(getVersionMessage.getSender(), versionMessage);

        return new ClusterFileGettingVersionState(getContainer(), getVersionMessage.getSender());
    }


    private State processNodeAddedMessage (NodeAddedMessage nodeAddedMessage) {
        getClusterFile().addNode(nodeAddedMessage.getNode());
        return this;
    }


    private State processNewNodeMessag (NewNodeMessage newNodeMessage) {
        NewNodeMessage newNodeMessage2 = new NewNodeMessage(getClusterFile().getQueue(), this, newNodeMessage);

        NodeElement nodeElement = new NodeElement(newNodeMessage.getDns(), newNodeMessage.getIp(), newNodeMessage.getPort());

        getClusterFile().addNode(nodeElement);

        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(getContainer(), getClusterFile());
        return clusterFileReadyState;
    }
}
