package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * A logical grouping of {@Link Node}.
 * <p>
 * <p>
 * This class allows the rest of the system to treat a cluster like a single unit.
 * For example the system can "tell" the cluster about a new message and
 * let the class worry about distributing it.
 * </P>
 * Created by Clark on 12/31/2016.
 */
public class Cluster extends Consumer {
    private Logger logger = Logger.getLogger(Cluster.class);

    private static Cluster ourInstance;


    public static synchronized void initializeClass(String filename, BlockingQueue<Message> writerQueue, BlockingQueue<Message> network) {
        if (null == ourInstance) {
            BlockingQueue<Message> clusterQueue = new LinkedBlockingQueue<Message>();

            ClusterFile.initialize(filename, writerQueue, clusterQueue);

            ourInstance = new Cluster(network, clusterQueue);
            ourInstance.start();
        }
    }

    public static Cluster getInstance() {
        return ourInstance;
    }

    public static synchronized void reset() {
        ourInstance = null;
    }

    private Cluster(BlockingQueue<Message> network, BlockingQueue<Message> queue) {
        super("cluster", queue);
        this.clusterFileQueue = ClusterFile.getInstance().getQueue();

        ClusterReadyState readyState = new ClusterReadyState(this);
        setCurrentState(readyState);

        this.network = network;
    }


    private List<Node> nodes = new ArrayList<Node>();
    private BlockingQueue<Message> network;
    private BlockingQueue<Message> clusterFileQueue;
    private NetworkListener networkListener;

    public BlockingQueue<Message> getClusterFileQueue() {
        return clusterFileQueue;
    }

    public void replaceClusterFileQueue (BlockingQueue<Message> newQueue) {
        this.clusterFileQueue = newQueue;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public NetworkListener getNetworkListener() {
        return networkListener;
    }

    public State start () {
        super.start();

        State state = new ClusterReadyState(this);
        setCurrentState(state);

        int port = Miranda.properties.getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        networkListener = new NetworkListener(port, getQueue());
        networkListener.listen();

        return getCurrentState();
    }


    public void load (String filename) {
        LoadMessage loadMessage = new LoadMessage(getInstance().getQueue(), filename, null);
        getInstance().send(loadMessage, getClusterFileQueue());
    }


    public boolean contains (NodeElement nodeElement) {
        for (Node node : nodes) {
            if (node.equalsElement(nodeElement))
                return true;
        }

        return false;
    }


    public void connect () {
        for (Node node : getNodes()) {
            if (!node.isConnected())
                node.connect();
        }
    }

    /**
     * Used for debugging only.
     *
     * @param newNodes
     */
    public void replaceNodes (List<Node> newNodes) {
        this.nodes = newNodes;
    }

    /**
     * Return the {@link Node} that matches the {@link NodeElement}
     */
    public Node matchingNode (NodeElement nodeElement) {
        for (Node node : getNodes()) {
            if (node.matches(nodeElement))
                return node;
        }

        return null;
    }
}
