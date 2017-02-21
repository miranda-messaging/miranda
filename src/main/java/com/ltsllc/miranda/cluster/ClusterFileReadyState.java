package com.ltsllc.miranda.cluster;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.SingleFileReadyState;
import com.ltsllc.miranda.network.NodeAddedMessage;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class ClusterFileReadyState extends SingleFileReadyState {
    private static Logger logger = Logger.getLogger(ClusterFileReadyState.class);

    private ClusterFile clusterFile;
    private BlockingQueue<Message> clusterQueue;

    public ClusterFileReadyState(ClusterFile clusterFile, BlockingQueue<Message> clusterQueue) {
        super(clusterFile);
        this.clusterFile = clusterFile;
        this.clusterQueue = clusterQueue;
    }

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    public BlockingQueue<Message> getClusterQueue() {
        return clusterQueue;
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
                nextState = processNewNodeMessage(newNodeMessage);
                break;
            }

            case WriteSucceeded: {
                break;
            }

            case WriteFailed: {
                WriteFailedMessage writeFailedMessage = (WriteFailedMessage) message;
                nextState = processWriteFailedMessage(writeFailedMessage);
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


    private State processNodeAddedMessage (NodeAddedMessage nodeAddedMessage) {
        getClusterFile().addNode(nodeAddedMessage.getNode());
        return this;
    }


    private State processNodeUpdated (NodeUpdatedMessage nodeUpdatedMessage) {
        getClusterFile().updateNode(nodeUpdatedMessage.getOldNode(), nodeUpdatedMessage.getNewNode());

        return this;
    }


    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        byte[] buffer = getClusterFile().getBytes();
        ClusterFileMessage clusterFileMessage = new ClusterFileMessage (getClusterFile().getQueue(), this, buffer, getClusterFile().getVersion());
        send(getClusterFileMessage.getSender(), clusterFileMessage);

        return this;
    }

    /**
     * This is called when there is a new version of the cluster file.  If
     * something changed, then we need to tell the cluster about it.
     *
     * @param newClusterFileMessage
     * @return
     */
    private State processNewClusterFileMessage (NewClusterFileMessage newClusterFileMessage) {
        if (!getClusterFile().getVersion().equals(newClusterFileMessage.getVersion())) {
            getClusterFile().merge(newClusterFileMessage.getFile());
        }

        return this;
    }

    /**
     * This message means that we should update all the matching nodes time
     * of last conection, and possibly drop the nodes that don't match.  A
     * node that has not connected in an amount of time (in milliseconds)
     * specifiede by {@link MirandaProperties#PROPERTY_CLUSTER_TIMEOUT}
     * should be dropped.
     *
     * @param healthCheckUpdateMessage
     * @return
     */
    private State processHealthCheckUpdateMessage (HealthCheckUpdateMessage healthCheckUpdateMessage) {
        //
        // update the time of last connect for nodes in the message
        //
        boolean nodesUpdated = false;
        for (NodeElement nodeElement : healthCheckUpdateMessage.getUpdates()) {
            NodeElement match = getClusterFile().matchingNode(nodeElement);
            if (null != match) {
                match.setLastConnected(System.currentTimeMillis());
                nodesUpdated = true;
            }
        }


        //
        // check to see if we should drop any nodes
        //
        long timeout = MirandaProperties.getInstance().getLongProperty(MirandaProperties.PROPERTY_CLUSTER_TIMEOUT);
        long now = System.currentTimeMillis();
        List<NodeElement> drops = new ArrayList<NodeElement>();
        for (NodeElement nodeElement : getClusterFile().getData()) {
            long timeSinceLastConnect = now - nodeElement.getLastConnected();
            if (timeSinceLastConnect >= timeout)
                drops.add(nodeElement);
        }

        //
        // drop nodes
        //
        if (drops.size() > 0) {
            logger.info("dropping nodes that have timed out: " + drops);
            getClusterFile().getData().removeAll(drops);
            getClusterFile().updateVersion();
            getClusterFile().write();

            for (NodeElement droppedNode : drops) {
                DropNodeMessage message = new DropNodeMessage(getClusterFile().getQueue(), this, droppedNode);
                send(getClusterQueue(), message);
            }
        }

        //
        // if we changed anything, update the version and write out the file
        //
        if (nodesUpdated || drops.size() > 0) {
            getClusterFile().updateVersion();
            getClusterFile().write();
        }

        return this;
    }

    /**
     * Send the version of the cluster file to the sender.
     *
     * @param getVersionMessage
     * @return
     */
    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("cluster", getClusterFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getClusterFile().getQueue(), this, nameVersion);
        send(getVersionMessage.getRequester(), versionMessage);

        return this;
    }


    @Override
    public Type getListType() {
        return new TypeToken<List<NodeElement>>(){}.getType();
    }


    @Override
    public State getSyncingState() {
        ClusterFileSyncingState clusterFileSyncingState = new ClusterFileSyncingState(getClusterFile());
        return clusterFileSyncingState;
    }


    public void write () {
        WriteMessage writeMessage = new WriteMessage(getClusterFile().getFilename(), getClusterFile().getBytes(), getClusterFile().getQueue(), this);
        send (getClusterFile().getWriterQueue(), writeMessage);
    }


    @Override
    public boolean contains(Object o) {
        NodeElement nodeElement = (NodeElement) o;
        return getClusterFile().contains(nodeElement);
    }


    @Override
    public Version getVersion() {
        return getClusterFile().getVersion();
    }


    @Override
    public void add(Object o) {
        NodeElement nodeElement = (NodeElement) o;
        getClusterFile().getData().add(nodeElement);
    }


    @Override
    public SingleFile getFile() {
        return getClusterFile();
    }


    @Override
    public String getName() {
        return "clusters";
    }


    @Override
    public List<Perishable> getPerishables() {
        return new ArrayList<Perishable>(getClusterFile().getData());
    }


    @Override
    public String toString() {
        return "ReadyState";
    }

    /**
     * A node just connected to us.  Add it to the file.
     *
     * @param newNodeMessage
     * @return
     */
    private State processNewNodeMessage (NewNodeMessage newNodeMessage) {
        Node node = newNodeMessage.getNode();
        NodeElement nodeElement = new NodeElement(node.getDns(), node.getIp(), node.getPort(), node.getDescription());

        if (!contains(nodeElement)) {
            getClusterFile().add(nodeElement);
            getClusterFile().updateVersion();
        } else {
            NodeElement temp = getClusterFile().matchingNode(nodeElement);
            temp.setLastConnected(System.currentTimeMillis());
        }

        return this;
    }


    private State processWriteFailedMessage (WriteFailedMessage message) {
        logger.error("Failed to write cluster file: " + message.getFilename(), message.getCause());

        return this;
    }
}
