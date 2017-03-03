package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.property.MirandaProperties;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to make switching between netty and sockets easier.
 *
 * <P>
 *     This class has the responsiblity of listening for new connections from
 *     other nodes. Subclases need to define the listen method.
 * </P>
 */
abstract public class NetworkListener extends Consumer {
    abstract public void startup() throws NetworkException;
    abstract public Handle nextConnection ();

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

    public NetworkListener () {
        super("network liastener");

        MirandaProperties properties = Miranda.properties;
        port = properties.getIntegerProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
    }

    public void start () {
        try {
            startup();
        } catch (NetworkException e) {
            StartupPanic startupPanic = new StartupPanic ("Exception in the NetworkListener during setup", e, StartupPanic.StartupReasons.NetworkListener);
            boolean continuePanic = Miranda.getInstance().panic(startupPanic);
            if (continuePanic) {
                shutdown();
            }
        }

        while (keepGoing()) {
            Handle newConnection = nextConnection();

            if (null != newConnection) {
                int handle = Network.getInstance().newConnection(newConnection);
                Node node = new Node(handle);
                node.start();

                NewConnectionMessage message = new NewConnectionMessage(getQueue(), this, node);
                send(message, Cluster.getInstance().getQueue());
                send(message, Miranda.getInstance().getQueue());
            }
        }

        setCurrentState(StopState.getInstance());
    }

    public void shutdown () {
        setKeepGoing(false);
    }
}
