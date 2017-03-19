package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.ClusterFileMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;

/**
 * Created by Clark on 2/21/2017.
 */
public class ClusterConnectingState extends State {
    private Cluster cluster;

    public ClusterConnectingState (Cluster cluster) {
        super(cluster);

        this.cluster = cluster;
    }

    public Cluster getCluster() {
        return cluster;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
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


    private State processClusterFileMessage (ClusterFileMessage clusterFileMessage) {
        for (NodeElement nodeElement : clusterFileMessage.getFile()) {
            if (!getCluster().contains(nodeElement)) {
                Node node = new Node(nodeElement, getCluster().getNetwork(), getCluster());
                node.start();
                node.connect();
            }
        }

        ClusterReadyState clusterReadyState = new ClusterReadyState(getCluster());
        return clusterReadyState;
    }
}
