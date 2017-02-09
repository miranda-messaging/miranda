package com.ltsllc.miranda.cluster;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.NewConnectionMessage;
import com.ltsllc.miranda.node.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */
public class ClusterSyncingState extends State {
    private static Logger logger = Logger.getLogger(ClusterSyncingState.class);
    private static Gson ourGson = new Gson();

    private Node node;
    private Cluster cluster;
    private BlockingQueue<Message> versionNotifier;


    public Cluster getCluster() {
        return cluster;
    }

    public BlockingQueue<Message> getVersionNotifier() {
        return versionNotifier;
    }

    public void setVersionNotifier(BlockingQueue<Message> versionNotifier) {
        this.versionNotifier = versionNotifier;
    }

    public Node getNode() {
        return node;
    }

    public ClusterSyncingState(Consumer consumer, Cluster cluster, Node node) {
        super(consumer);
        this.cluster = cluster;
        this.node = node;

        assert (null != this.cluster);
        assert (null != this.node);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NewConnection: {
                NewConnectionMessage newConnectionMessage = (NewConnectionMessage) message;
                nextState = processNewConnectionMessage(newConnectionMessage);
                break;
            }

            case NodeUpdated: {
                NodeUpdatedMessage nodeUpdatedMessage = (NodeUpdatedMessage) message;
                nextState = prcessNodeUpdatedMessage(nodeUpdatedMessage);
                break;
            }

            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage(versionMessage);
                break;
            }

            case NodesLoaded: {
                NodesLoadedMessage nodesLoadedMessage = (NodesLoadedMessage) message;
                nextState = processNodesLoadedMessage(nodesLoadedMessage);
                break;
            }

            case Versions: {
                VersionsMessage versionsMessage = (VersionsMessage) message;
                nextState = processVersionsMessage(versionsMessage);
                break;
            }

            case ClusterFile: {
                ClusterFileMessage clusterFileMessage = (ClusterFileMessage) message;
                nextState = processClusterFileMessage(clusterFileMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    @Override
    public State start() {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getCluster().getQueue(), this);
        send(getNode().getQueue(), getVersionMessage);

        return this;
    }

    private State processVersionMessage(VersionMessage versionMessage) {
        VersionMessage versionMessage2 = new VersionMessage(getCluster().getQueue(), getCluster(), versionMessage.getNameVersion());
        send(node.getQueue(), versionMessage2);

        return this;
    }

    private State processGetVersionMessage(GetVersionMessage getVersionMessage) {
        State nextState = this;

        setVersionNotifier(getVersionMessage.getSender());

        GetVersionMessage getVersionMessage2 = new GetVersionMessage(getCluster().getQueue(), this);
        send(getCluster().getClusterFile().getQueue(), getVersionMessage2);

        return nextState;
    }


    private State prcessNodeUpdatedMessage(NodeUpdatedMessage nodeUpdatedMessage) {
        NodeUpdatedMessage nodeUpdatedMessage2 = new NodeUpdatedMessage(getCluster().getQueue(), this, getNode());
        send(getCluster().getClusterFile().getQueue(), nodeUpdatedMessage2);

        return this;
    }


    private State processNewConnectionMessage(NewConnectionMessage newConnectionMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getCluster().getQueue(), this);
        send(newConnectionMessage.getNode().getQueue(), getVersionMessage);

        return new ClusterSyncingState(getCluster(), getCluster(), newConnectionMessage.getNode());
    }


    /**
     * This is called when the cluster receives a version of the cluster file
     * that it requested from a newly connected node.  It needs to determine if
     * the remote file is more recent than the file it has.
     *
     * @param versionsMessage
     * @return
     */
    private State processVersionsMessage(VersionsMessage versionsMessage) {
        for (NameVersion nameVersion : versionsMessage.getVersions()) {
            if (!nameVersion.getName().equals("cluster"))
                continue;

            if (null == getCluster().getClusterFile().getVersion()) {
                GetClusterFileMessage getClusterFileMessage = new GetClusterFileMessage(getCluster().getQueue(), this);
                send(versionsMessage.getSender(), getClusterFileMessage);
            } else if (nameVersion.getVersion().equals(getCluster().getClusterFile().getVersion())) {
                return new ClusterReadyState(getCluster());
            } else {
                if (nameVersion.getVersion().isMoreRecent(getCluster().getClusterFile().getVersion())) {
                    GetClusterFileMessage getClusterFileMessage = new GetClusterFileMessage(getCluster().getQueue(), this);

                    try {
                        getNode().getQueue().put(getClusterFileMessage);
                    } catch (InterruptedException e) {
                        logger.fatal("Exception while trying to send message", e);
                        System.exit(1);
                    }
                }
            }
        }

        return this;
    }

    private State processNodesLoadedMessage(NodesLoadedMessage nodesLoadedMessage) {
        State nextState = this;

        getCluster().getClusterFile().nodesLoaded(nodesLoadedMessage.getNodes());

        return nextState;
    }

    /**
     * Called when we have determined that a remote cluster file is more recent than our
     * local cluster file.
     *
     * @param clusterFileMessage
     * @return
     */
    private State processClusterFileMessage(ClusterFileMessage clusterFileMessage) {
        ArrayList<NodeElement> arrayList = new ArrayList<NodeElement>();
        String json = new String(clusterFileMessage.getBuffer());
        arrayList = ourGson.fromJson(json, arrayList.getClass());
        NewClusterFileMessage newClusterFileMessage = new NewClusterFileMessage(getCluster().getQueue(), this, arrayList, clusterFileMessage.getVersion());
        send(getCluster().getClusterFile().getQueue(), newClusterFileMessage);

        return this;
    }
}
