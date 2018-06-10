package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.network.Network;

public class ConnectionListener extends Consumer {
    private Cluster cluster;
    private Network network;

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

    public ConnectionListener (Cluster cluster, Network network) {
        setCluster(cluster);
        setNetwork(network);
    }
}
