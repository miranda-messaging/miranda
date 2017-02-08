package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.NodeAddedMessage;
import com.ltsllc.miranda.node.GetVersionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.VersionMessage;

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

            case NodeAdded: {
                NodeAddedMessage nodeAddedMessage = (NodeAddedMessage) m;
                getCluster().addNewNode(nodeAddedMessage.getNode());
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

            default:
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        GetVersionMessage getVersionMessage2 = new GetVersionMessage(getCluster().getQueue(), this);
        send (getCluster().getClusterFile().getQueue(), getVersionMessage2);

        GettingVersionState gettingVersionState = new GettingVersionState(getContainer(), getCluster().getClusterFile(), getVersionMessage.getSender());
        return gettingVersionState;
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
            getCluster().addNode(element);
        }

        return nextState;
    }

    /**
     * Tell the nodes in the cluster to connect.
     */
    private void processConnect() {
        getCluster().connect();
    }



}
