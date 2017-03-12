package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.LoadResponseMessage;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.property.MirandaProperties;

import java.util.List;

/**
 * Created by Clark on 3/10/2017.
 */
public class ClusterStartingState extends State {
    public ClusterStartingState (Cluster cluster) {
        super(cluster);
    }

    public Cluster getCluster () {
        return (Cluster) getContainer();
    }

    public State start () {
        String filename = Miranda.properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);
        LoadMessage loadMessage = new LoadMessage(getCluster().getQueue(), filename, this);
        send(getCluster().getClusterFileQueue(), loadMessage);

        return this;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case LoadResponse: {
                LoadResponseMessage loadResponseMessage = (LoadResponseMessage) message;
                nextState = processLoadResponseMessage(loadResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }

    private State processLoadResponseMessage (LoadResponseMessage loadResponseMessage) {
        List<NodeElement> nodes = loadResponseMessage.getData();
        getCluster().merge(nodes);
        getCluster().connect();

        ClusterConnectingState clusterConnectingState = new ClusterConnectingState(getCluster());
        return clusterConnectingState;
    }
}
