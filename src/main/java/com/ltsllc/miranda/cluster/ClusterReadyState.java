package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.node.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 1/3/2017.
 */

/**
 * The cluster is ready to accept commands
 */
public class ClusterReadyState extends State {
    private static Logger logger = Logger.getLogger(ClusterReadyState.class);

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
                ConnectMessage connectMessage = (ConnectMessage) m;
                nextState = processConnectMessage(connectMessage);
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

            case DropNode: {
                DropNodeMessage dropNodeMessage = (DropNodeMessage) m;
                nextState = processDropNodeMessage (dropNodeMessage);
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

        send(getCluster().getClusterFileQueue(), loadMessage);

        return nextState;
    }


    private State processNodesLoaded(NodesLoadedMessage nodesLoadedMessage)
    {
        State nextState = this;

        //
        // determine if we need to connect to a new node
        //
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
    private State processConnectMessage(ConnectMessage connectMessage) {
        getCluster().connect();

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
                node.connect();
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
        send (getCluster().getClusterFileQueue(), healthCheckUpdateMessage);

        return this;
    }


    private boolean contains (NodeElement nodeElement) {
        for (Node node : getCluster().getNodes()) {
            if (node.equals(nodeElement))
                return true;
        }

        return false;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        GetVersionMessage getVersionMessage2 = new GetVersionMessage(getCluster().getQueue(), this, getVersionMessage.getRequester());
        send(getCluster().getClusterFileQueue(), getVersionMessage2);

        return this;
    }

    /**
     * A {@link NodeElement} has "timed out" and been dropped from the cluster file.
     *
     * @param dropNodeMessage
     * @return
     */
    private State processDropNodeMessage (DropNodeMessage dropNodeMessage) {
        Node node = getCluster().matchingNode(dropNodeMessage.getDroppedNode());

        if (null != node) {
            if (node.isConnected()) {
                logger.warn ("Asked to drop a connected node (" + dropNodeMessage.getDroppedNode() + "), ignoring");
            } else {
                logger.info("Dropping node from cluster: " + dropNodeMessage.getDroppedNode());
                getCluster().getNodes().remove(node);
            }
        }

        return this;
    }
}
