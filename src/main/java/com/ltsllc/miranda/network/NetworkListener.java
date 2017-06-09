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
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class to make switching between netty and sockets easier.
 *
 * <P>
 *     This class has the responsiblity of listening for new connections from
 *     other nodes. Subclases need to define the listen method.
 * </P>
 */
abstract public class NetworkListener extends Consumer {
    abstract public void stopListening();

    public static final String NAME = "network listener";

    private int port;
    private boolean keepGoing = true;
    private int connectionCount;
    private BlockingQueue<Handle> handleQueue;

    public BlockingQueue<Handle> getHandleQueue() {
        return handleQueue;
    }

    public int getPort() {
        return port;
    }

    public boolean keepGoing () {
        return keepGoing;
    }

    public void setKeepGoing (boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public void incrementConnectionCount () {
        connectionCount++;
    }

    public NetworkListener (int port) {
        super("network listener");

        this.port = port;
        this.connectionCount = 0;
        this.handleQueue = new LinkedBlockingQueue<Handle>();

        NetworkListenerReadyState readyState = new NetworkListenerReadyState(this);
        setCurrentState(readyState);
    }

    public void newConnectionLoop (BlockingQueue<Handle> handleQueue) {
        while (keepGoing()) {
            Handle newConnection = null;

            try {
                newConnection = handleQueue.take();
            } catch (InterruptedException e) {
                Panic panic = new Panic("Exception getting new connection", e, Panic.Reasons.ExceptionWaitingForNextConnection);
                Miranda.getInstance().panic(panic);
            }

            if (null != newConnection) {
                int handleID = Network.getInstance().newConnection(newConnection);

                Node node = new Node(handleID, Network.getInstance(), Miranda.getInstance().getCluster());
                newConnection.setQueue(node.getQueue());
                node.start();

                incrementConnectionCount();
            }
        }
    }

    public void shutdown () {
        setKeepGoing(false);
        getThread().interrupt();
        setCurrentState(StopState.getInstance());
    }
}
