package com.ltsllc.miranda.network;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;

/**
 * Listen for unencrypted connections
 */
abstract public class UnencryptedConnectionListener extends ConnectionListener {


    public UnencryptedConnectionListener (Network network, Cluster cluster, int port) throws MirandaException {
        super(port, network, cluster);
    }

}
