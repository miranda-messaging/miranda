package com.ltsllc.miranda.actions;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.network.Network;

/**
 * A class that "knows" how to join a cluster.
 */
public class JoinClusterAction extends Consumer {
    private Network network;
    private Cluster cluster;

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    JoinClusterAction(Network network, Cluster cluster) throws MirandaException {
        super("join cluster");
        setCluster(cluster);
        setNetwork(network);
        JoinClusterState joinClusterState = new JoinClusterState(this);
        setCurrentState(joinClusterState);
    }


}
