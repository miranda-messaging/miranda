package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.network.NodeAddedMessage;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.writer.WriteMessage;

import java.util.ArrayList;
import java.util.List;

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

            case NewNode: {
                NewNodeMessage newNodeMessage = (NewNodeMessage) message;
                nextState = processNewNodeMessag(newNodeMessage);
                break;
            }

            case WriteSucceeded: {
                break;
            }

            case NodeUpdated: {
                NodeUpdatedMessage nodeUpdatedMessage = (NodeUpdatedMessage) message;
                nextState = processNodeUpdated(nodeUpdatedMessage);
                break;
            }

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) message;
                nextState = processGetClusterFileMessage(getClusterFileMessage);
                break;
            }

            case NewClusterFile: {
                NewClusterFileMessage newClusterFileMessage = (NewClusterFileMessage) message;
                nextState = processNewClusterFileMessage (newClusterFileMessage);
                break;
            }

            case HealthCheckUpdate: {
                HealthCheckUpdateMessage healthCheckUpdateMessage = (HealthCheckUpdateMessage) message;
                nextState = processHealthCheckUpdateMessage(healthCheckUpdateMessage);
                break;
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
        NodeElement nodeElement = new NodeElement(newNodeMessage.getDns(), newNodeMessage.getIp(), newNodeMessage.getPort(), newNodeMessage.getDescription());

        getClusterFile().addNode(nodeElement);

        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(getContainer(), getClusterFile());
        return clusterFileReadyState;
    }


    private State processNodeUpdated (NodeUpdatedMessage nodeUpdatedMessage) {
        byte[] buffer = getClusterFile().getBytes();
        WriteMessage writeMessage = new WriteMessage(getClusterFile().getFilename(), buffer, getClusterFile().getQueue(), this);
        send(getClusterFile().getWriterQueue(), writeMessage);

        return this;
    }


    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        byte[] buffer = getClusterFile().getBytes();
        ClusterFileMessage clusterFileMessage = new ClusterFileMessage (getClusterFile().getQueue(), this, buffer, getClusterFile().getVersion());
        send(getClusterFileMessage.getSender(), clusterFileMessage);

        return this;
    }

    /**
     * This is called when there is a new version of the cluster file.  We
     * need to determine the node that we are connected to and connect to
     * the new nodes.
     *
     * @param newClusterFileMessage
     * @return
     */
    private State processNewClusterFileMessage (NewClusterFileMessage newClusterFileMessage) {
        for (NodeElement element : getClusterFile().getData()) {
            if (!getClusterFile().contains(element)) {
                Node node = new Node(element);
                node.start();

                NewNodeMessage newNodeMessage = new NewNodeMessage(getClusterFile().getQueue(), this, node);
                send(Cluster.getInstance().getQueue(), newNodeMessage);
            }
        }

        getClusterFile().setData(newClusterFileMessage.getFile());
        getClusterFile().setVersion(newClusterFileMessage.getVersion());

        ClusterFileChangedMessage clusterFileChangedMessage = new ClusterFileChangedMessage(getClusterFile().getQueue(), this, newClusterFileMessage.getFile(), newClusterFileMessage.getVersion());
        send(Cluster.getInstance().getQueue(), clusterFileChangedMessage);

        return this;
    }


    private State processHealthCheckUpdateMessage (HealthCheckUpdateMessage healthCheckUpdateMessage) {
        long timeout = MirandaProperties.getInstance().getLongProperty(MirandaProperties.PROPERTY_CLUSTER_TIMEOUT);

        for (NodeElement nodeElement : healthCheckUpdateMessage.getUpdates()) {
            getClusterFile().updateNode(nodeElement);
        }

        List<NodeElement> drops = new ArrayList<NodeElement>();
        for (NodeElement nodeElement : getClusterFile().getData()) {
            if (nodeElement.hasTimedout(timeout))
                drops.add(nodeElement);
        }

        for (NodeElement nodeElement : drops) {
            for (NodeElement element : getClusterFile().getData()) {
                if (element.equals(nodeElement))
                    getClusterFile().getData().remove(element);
            }
        }

        WriteMessage writeMessage = new WriteMessage(getClusterFile().getFilename(), getClusterFile().getBytes(), getClusterFile().getQueue(), this);
        send(getClusterFile().getWriterQueue(), writeMessage);

        return this;

    }
}
