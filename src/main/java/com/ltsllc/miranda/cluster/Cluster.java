package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.cluster.messages.NodesUpdatedMessage;
import com.ltsllc.miranda.netty.NettyNetworkListener;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.servlet.ClusterStatusObject;
import com.ltsllc.miranda.servlet.NodeStatus;
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


    public static synchronized void initializeClass(String filename, BlockingQueue<Message> writerQueue, Network network) {
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

    private Cluster(Network network, BlockingQueue<Message> queue) {
        super("cluster", queue);
        this.clusterFileQueue = ClusterFile.getInstance().getQueue();

        ClusterStartingState clusterStartingState = new ClusterStartingState(this);
        setCurrentState(clusterStartingState);

        this.network = network;
    }


    private List<Node> nodes = new ArrayList<Node>();
    private Network network;
    private BlockingQueue<Message> clusterFileQueue;
    private NettyNetworkListener networkListener;

    public BlockingQueue<Message> getClusterFileQueue() {
        return clusterFileQueue;
    }

    public void replaceClusterFileQueue (BlockingQueue<Message> newQueue) {
        this.clusterFileQueue = newQueue;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Network getNetwork() {
        return network;
    }

    public NettyNetworkListener getNetworkListener() {
        return networkListener;
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

    public ClusterStatusObject getStatus () {
        List<NodeStatus> nodes = new ArrayList<NodeStatus>(getNodes().size());

        for (Node node : getNodes()) {
            NodeStatus.NodeStatuses status = node.isConnected() ? NodeStatus.NodeStatuses.Online : NodeStatus.NodeStatuses.Online;
            NodeStatus nodeStatus = new NodeStatus(node.getDns(), node.getIp(), node.getPort(), node.getDescription(), status);
        }

        return new ClusterStatusObject(nodes);
    }

    public void merge (List<NodeElement> newNodes) {
        boolean update = false;

        List<NodeElement> reallyNewNodes = new ArrayList<NodeElement>();
        for (NodeElement element : newNodes) {
            if (!contains(element)) {
                reallyNewNodes.add(element);
                update = true;
            }
        }

        if (update) {
            for (NodeElement element : reallyNewNodes) {
                Node node = new Node(element, getNetwork());
                node.start();
                getNodes().add(node);
            }

            List<NodeElement> nodeList = asNodeElements();
            NodesUpdatedMessage nodesUpdatedMessage = new NodesUpdatedMessage(getQueue(), this, nodeList);
            send(nodesUpdatedMessage, getClusterFileQueue());
        }
    }

    /**
     * This gets called when a node connects "out of the blue."
     *
     * @param node The node that just connected.
     */
    public void newNode (Node node) {

    }

    /**
     * Tell the cluster about the new node.
     */
    public void sendNewNode (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        NewNodeMessage newNodeMessage = new NewNodeMessage(senderQueue, sender, node);
        sendToMe(newNodeMessage);
    }

    public void load () {
        LoadMessage loadMessage = new LoadMessage(getQueue(), "junk", this);
    }

    public void performHealthCheck () {
        long now = System.currentTimeMillis();

        boolean updated = false;
        List<NodeElement> nodeList = new ArrayList<NodeElement>(getNodes().size());
        for (Node node : getNodes()) {
            NodeElement nodeElement = node.asNodeElement();
            if (node.isConnected()) {
                nodeElement.setLastConnected(now);
                updated = true;
            }
        }

        if (updated) {
            NodesUpdatedMessage nodesUpdatedMessage = new NodesUpdatedMessage (getQueue(), this, nodeList);
            send(nodesUpdatedMessage, getClusterFileQueue());
        }
    }

    public List<NodeElement> asNodeElements () {
        List<NodeElement> nodeElements = new ArrayList<NodeElement>(getNodes().size());

        for (Node node : getNodes()) {
            NodeElement nodeElement = node.asNodeElement();
            nodeElements.add(nodeElement);
        }

        return nodeElements;
    }
}
