/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.mina.states.ConnectionListenerReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class to make switching between netty and sockets easier.
 * <p>
 * <p>
 * This class has the responsiblity of listening for new connections from
 * other nodes. Subclases need to define the listen method.
 * </P>
 */
abstract public class ConnectionListener extends Consumer {
    abstract public void stopListening();
    abstract public void startListening();

    public static final String NAME = "network listener";

    private Network network;
    private Cluster cluster;
    private int port;
    private boolean keepGoing = true;
    private int connectionCount;
    private Thread connectionThread;

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

    public Thread getConnectionThread() {
        return connectionThread;
    }

    public void setConnectionThread(Thread connectionThread) {
        this.connectionThread = connectionThread;
    }

    public int getPort() {
        return port;
    }

    public boolean keepGoing() {
        return keepGoing;
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public void incrementConnectionCount() {
        connectionCount++;
    }

    public ConnectionListener(int port, Network network, Cluster cluster) throws MirandaException {
        super("network listener");

        this.port = port;
        this.connectionCount = 0;
        setNetwork(network);
        setCluster(cluster);


        // ConnectionListenerReadyState readyState = new ConnectionListenerReadyState(this);
        // setCurrentState(readyState);
    }

}
