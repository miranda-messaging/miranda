package com.ltsllc.miranda.cluster;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.SingleFileReadyState;
import com.ltsllc.miranda.miranda.SynchronizeMessage;
import com.ltsllc.miranda.node.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 1/3/2017.
 */

/**
 * The cluster is ready to accept commands
 */
public class ClusterReadyState extends State {
    private Cluster cluster;

    public ClusterReadyState(Cluster cluster) {
        super(cluster);

        this.cluster = cluster;

        assert(null != this.cluster);
    }

    public Cluster getCluster() {
        return cluster;
    }

    public State processMessage(Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case Load: {
                LoadMessage loadMessage = (LoadMessage) m;
                nextState = processLoad(loadMessage);
                break;
            }

            case Connect: {
                processConnect();
                break;
            }

            case NodesLoaded: {
                NodesLoadedMessage nodesLoaded = (NodesLoadedMessage) m;
                nextState = processNodesLoaded(nodesLoaded);
                break;
            }

            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) m;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case NewNode: {
                NewNodeMessage newNodeMessage = (NewNodeMessage) m;
                nextState = processNewNodeMessage (newNodeMessage);
                break;
            }

            case ClusterFileChanged: {
                ClusterFileChangedMessage clusterFileChangedMessage = (ClusterFileChangedMessage) m;
                nextState = processClusterFileChangedMessage (clusterFileChangedMessage);
                break;
            }

            case HealthCheck: {
                HealthCheckMessage healthCheckMessage = (HealthCheckMessage) m;
                nextState = processHealthCheck(healthCheckMessage);
                break;
            }

            case Synchronize: {
                SynchronizeMessage synchronizeMessage = (SynchronizeMessage) m;
                nextState = processSynchronizeMessage(synchronizeMessage);
                break;
            }


            default:
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }




    private State processLoad (LoadMessage loadMessage) {
        State nextState = this;

        send(getCluster().getClusterFile().getQueue(), loadMessage);

        return nextState;
    }


    private State processNodesLoaded(NodesLoadedMessage nodesLoadedMessage)
    {
        State nextState = this;

        for (NodeElement element : nodesLoadedMessage.getNodes()) {
            if (!contains(element)) {
                Node node = new Node(element, getCluster().getNetwork());
                node.start();
                node.connect();
                getCluster().getNodes().add(node);
            }
        }

        return nextState;
    }

    /**
     * Tell the nodes in the cluster to connect.
     */
    private void processConnect() {
        getCluster().connect();
    }


    private State processNewNodeMessage (NewNodeMessage newNodeMessage) {
        getCluster().getNodes().add(newNodeMessage.getNode());

        return this;
    }

    /**
     * This is called when the cluster file has one or more new nodes.
     *
     * @param clusterFileChangedMessage
     * @return
     */
    private State processClusterFileChangedMessage (ClusterFileChangedMessage clusterFileChangedMessage) {
        for (NodeElement nodeElement : clusterFileChangedMessage.getFile()) {
            if (!getCluster().contains(nodeElement)) {
                Node node = new Node (nodeElement, getCluster().getNetwork());
                node.start();
                getCluster().getNodes().add(node);
            }
        }

        return this;
    }

    private State processHealthCheck (HealthCheckMessage healthCheckMessage) {
        List<NodeElement> list = new ArrayList<NodeElement>();

        for (Node node : getCluster().getNodes()) {
            if (node.isConnected()) {
                NodeElement nodeElement = node.getUpdatedElement();
                list.add(nodeElement);
            }
        }

        HealthCheckUpdateMessage healthCheckUpdateMessage = new HealthCheckUpdateMessage(getCluster().getQueue(),this, list);
        send (getCluster().getClusterFile().getQueue(), healthCheckUpdateMessage);

        return this;
    }


    private State processSynchronizeMessage (SynchronizeMessage synchronizeMessage) {
        SynchronizeMessage synchronizeMessage2 = new SynchronizeMessage(getCluster().getQueue(), this, synchronizeMessage.getNode());
        send (getCluster().getClusterFile().getQueue(), synchronizeMessage2);

        return this;
    }

    private boolean contains (NodeElement nodeElement) {
        for (Node node : getCluster().getNodes()) {
            if (node.equals(nodeElement))
                return true;
        }

        return false;
    }

    private State processRemoteVersion (RemoteVersionMessage remoteVersion) {
        if (
                null == getCluster().getClusterFile().getVersion()
                || !getCluster().getClusterFile().getVersion().equals(remoteVersion.getVersion())
                )
        {
            GetClusterFileMessage getClusterFileMessage = new GetClusterFileMessage(getCluster().getQueue(), this);
            send(remoteVersion.getNode(), getClusterFileMessage);
        }

        ClusterSyncingState clusterSyncingState = new ClusterSyncingState(getCluster(), null);
        return clusterSyncingState;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("cluster", getCluster().getClusterFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getCluster().getQueue(), this, nameVersion);
        send(getVersionMessage.getSender(), versionMessage);

        return this;
    }
}
