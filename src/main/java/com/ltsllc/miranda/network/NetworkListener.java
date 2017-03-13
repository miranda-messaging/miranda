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

    private int port;
    private boolean keepGoing = true;

    public int getPort() {
        return port;
    }

    public boolean keepGoing () {
        return keepGoing;
    }

    public void setKeepGoing (boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public NetworkListener (int port) {
        super("network listener");

        this.port = port;

        NetworkListenerReadyState readyState = new NetworkListenerReadyState(this);
        setCurrentState(readyState);
    }

    public void getConnections () {
        LinkedBlockingQueue<Handle> queue = new LinkedBlockingQueue<Handle>();

        try {
            startup(queue);
        } catch (NetworkException e) {
            StartupPanic startupPanic = new StartupPanic ("Exception in the NetworkListener during setup", e, StartupPanic.StartupReasons.NetworkListener);
            boolean continuePanic = Miranda.getInstance().panic(startupPanic);
            if (continuePanic) {
                shutdown();
            }
        }

        while (keepGoing()) {
            Handle newConnection = null;

            try {
                newConnection = queue.take();
            } catch (InterruptedException e) {
                Panic panic = new Panic("Exception getting new connection", e, Panic.Reasons.ExceptionWaitingForNextConnection);
                if (Miranda.getInstance().panic(panic))
                    setKeepGoing(false);
            }

            if (null != newConnection) {
                int handleID = Network.getInstance().newConnection(newConnection);

                Node node = new Node(handleID, Network.getInstance(), Cluster.getInstance());
                newConnection.setQueue(node.getQueue());
                node.start();
            }
        }

        setCurrentState(StopState.getInstance());
    }

    public void shutdown () {
        setKeepGoing(false);
    }
}
