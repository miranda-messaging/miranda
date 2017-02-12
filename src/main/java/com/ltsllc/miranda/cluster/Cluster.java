package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.network.NodeAddedMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


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
            ClusterFile.initialize(filename, writerQueue);
            ourInstance = new Cluster(network);
        }
    }

    public static Cluster getInstance() {
        return ourInstance;
    }

    private Cluster(BlockingQueue<Message> network) {
        super("Cluster");
        this.clusterFile = ClusterFile.getInstance();

        ClusterReadyState readyState = new ClusterReadyState(this);
        setCurrentState(readyState);

        this.network = network;

        assert (null != this.network);
        assert (null != this.clusterFile);
    }


    private List<Node> nodes = new ArrayList<Node>();
    private BlockingQueue<Message> network;
    private ClusterFile clusterFile;
    private NetworkListener networkListener;

    public ClusterFile getClusterFile() {
        return clusterFile;
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

        int port = PropertiesUtils.getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        networkListener = new NetworkListener(port, getQueue());
        networkListener.listen();

        return getCurrentState();
    }


    public void load (String filename) {
        LoadMessage loadMessage = new LoadMessage(getInstance().getQueue(), filename, null);
        getInstance().send(loadMessage, getClusterFile().getQueue());
    }

    public void addNode (NodeElement nodeElement) {
        if (!contains(nodeElement)) {
            Node node = new Node(nodeElement, getNetwork());
            node.start();
            node.connect(this.getQueue(), this);
        }
    }

    public boolean contains (NodeElement nodeElement) {
        for (Node node : nodes) {
            if (node.equalsElement(nodeElement))
                return true;
        }

        return false;
    }

    public void connect () {
        long now = System.currentTimeMillis();

        for (NodeElement nodeElement : getClusterFile().getData()) {
            if (!nodeElement.expired(now)) {
                Node node = new Node(nodeElement, getNetwork());
                node.start();
                getNodes().add(node);
                node.connect();
            }
        }
    }
}
