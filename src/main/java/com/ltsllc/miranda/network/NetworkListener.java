package com.ltsllc.miranda.network;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
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
    abstract public void startup (BlockingQueue<Handle> queue) throws NetworkException;
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

    public void performStartup (BlockingQueue<Handle> handleQueue) {
        try {
            startup(handleQueue);
        } catch (NetworkException e) {
            StartupPanic startupPanic = new StartupPanic ("Exception in the NetworkListener during setup", e, StartupPanic.StartupReasons.NetworkListener);
            boolean continuePanic = Miranda.getInstance().panic(startupPanic);
            if (continuePanic) {
                shutdown();
            }
        }
    }

    public void newConnectionLoop (BlockingQueue<Handle> handleQueue) {
        while (keepGoing()) {
            Handle newConnection = null;

            try {
                newConnection = handleQueue.take();
            } catch (InterruptedException e) {
                Panic panic = new Panic("Exception getting new connection", e, Panic.Reasons.ExceptionWaitingForNextConnection);
                if (Miranda.getInstance().panic(panic))
                    setKeepGoing(false);
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

    public void getConnections () {
        performStartup(getHandleQueue());

        newConnectionLoop(getHandleQueue());

        setCurrentState(StopState.getInstance());
    }

    public void shutdown () {
        setKeepGoing(false);
        getThread().interrupt();
        setCurrentState(StopState.getInstance());
    }
}
