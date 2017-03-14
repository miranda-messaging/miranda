package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.LoadResponseMessage;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.*;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.servlet.ClusterStatusObject;
import com.ltsllc.miranda.servlet.GetStatusMessage;
import com.ltsllc.miranda.servlet.GetStatusResponseMessage;
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

            case LoadResponse: {
                LoadResponseMessage loadResponseMessage = (LoadResponseMessage) m;
                nextState = processLoadResponseMessage (loadResponseMessage);
                break;
            }

            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) m;
                nextState = processConnectMessage(connectMessage);
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

            case GetStatus: {
                GetStatusMessage getStatusMessage = (GetStatusMessage) m;
                nextState = processGetStatusMessage (getStatusMessage);
                break;
            }

            case NewNode: {
                NewNodeMessage newNodeMessage = (NewNodeMessage) m;
                nextState = processNewNodeMessage(newNodeMessage);
                break;
            }

            default:
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }




    private State processLoad (LoadMessage loadMessage) {
        getCluster().load();

        return this;
    }


    private State processNodesLoaded(NodesLoadedMessage nodesLoadedMessage)
    {
        getCluster().merge(nodesLoadedMessage.getNodes());

        return this;
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
        getCluster().merge(clusterFileChangedMessage.getFile());

        return this;
    }

    private State processHealthCheck (HealthCheckMessage healthCheckMessage) {
        getCluster().performHealthCheck();

        return this;
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


    private State processGetStatusMessage (GetStatusMessage getStatusMessage) {
        ClusterStatusObject clusterStatusObject = getCluster().getStatus();
        GetStatusResponseMessage response = new GetStatusResponseMessage(getCluster().getQueue(), this, clusterStatusObject);
        getStatusMessage.reply(response);

        return this;
    }

    private State processNewNodeMessage (NewNodeMessage newNodeMessage) {
        getCluster().getNodes().add(newNodeMessage.getNode());

        return this;
    }

    private State processLoadResponseMessage (LoadResponseMessage loadResponseMessage) {
        getCluster().merge(loadResponseMessage.getData());

        return this;
    }
}
