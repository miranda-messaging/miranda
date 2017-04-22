package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.cluster.messages.NewNodeMessage;
import com.ltsllc.miranda.cluster.messages.NodeStoppedMessage;
import com.ltsllc.miranda.cluster.messages.NodesUpdatedMessage;
import com.ltsllc.miranda.cluster.states.ClusterLoadingState;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.servlet.objects.ClusterStatusObject;
import com.ltsllc.miranda.servlet.messages.GetStatusMessage;
import com.ltsllc.miranda.servlet.objects.NodeStatus;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.topics.Topic;
import com.ltsllc.miranda.topics.messages.NewTopicMessage;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.messages.DeleteUserMessage;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.user.messages.UpdateUserMessage;
import com.ltsllc.miranda.writer.Writer;
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
    public static final String FILE_NAME = "cluster";

    private Logger logger = Logger.getLogger(Cluster.class);

    private static Cluster ourInstance;

    private List<Node> nodes = new ArrayList<Node>();
    private Network network;
    private ClusterFile clusterFile;

    public static synchronized void initializeClass(String filename, Writer writer, Network network) {
        if (null == ourInstance) {
            BlockingQueue<Message> clusterQueue = new LinkedBlockingQueue<Message>();

            ClusterFile.initialize(filename, writer, clusterQueue);

            ourInstance = new Cluster(network, ClusterFile.getInstance());
            ourInstance.start();
        }
    }

    public static Cluster getInstance() {
        return ourInstance;
    }

    // for testing
    public static void setInstance (Cluster cluster) {
        ourInstance = cluster;
    }

    public static synchronized void reset() {
        ourInstance = null;
    }

    public Cluster(Network network, ClusterFile clusterFile) {
        super("cluster");

        this.clusterFile = clusterFile;

        ClusterLoadingState clusterLoadingState = new ClusterLoadingState(this);
        setCurrentState(clusterLoadingState);

        this.network = network;
    }

    public BlockingQueue<Message> getClusterFileQueue() {
        return clusterFile.getQueue();
    }

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    public void setClusterFile(ClusterFile clusterFile) {
        this.clusterFile = clusterFile;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Network getNetwork() {
        return network;
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
     * When the cluster is starting
     *
     * @param newNodes
     */
    public void replaceNodes (List<NodeElement> newNodes) {
        List<Node> nodeList = new ArrayList<Node>();

        for (NodeElement nodeElement : newNodes) {
            Node node = new Node(nodeElement, getNetwork(), this);
            node.start();
            nodeList.add(node);
        }

        this.nodes = nodeList;
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
        List<NodeStatus> statusOfNodes = new ArrayList<NodeStatus>(getNodes().size());

        for (Node node : getNodes()) {
            NodeStatus status = node.getStatus();
            statusOfNodes.add(status);
        }

        return new ClusterStatusObject(statusOfNodes);
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
                Node node = new Node(element, getNetwork(), this);
                node.start();
                node.connect();
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
     * <p>
     *     Add the node, then tell the cluster file we have changed
     * </p>
     *
     * @param node The node that just connected.
     */
    public void newNode (Node node) {
        getNodes().add(node);

        List<NodeElement> nodeElementList = asNodeElements();
        NodesUpdatedMessage message = new NodesUpdatedMessage(getQueue(), this, nodeElementList);

        send (message, getClusterFileQueue());
    }

    /**
     * Tell the cluster about the new node.
     */
    public void sendNewNode (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        NewNodeMessage newNodeMessage = new NewNodeMessage(senderQueue, sender, node);
        sendToMe(newNodeMessage);
    }

    public void load () {
        getClusterFile().sendLoad(getQueue(), this);
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

    public void sendConnect (BlockingQueue<Message> senderQueue, Object sender) {
        ConnectMessage connectMessage = new ConnectMessage(senderQueue, sender);
        sendToMe(connectMessage);
    }

    public void sendGetStatus (BlockingQueue<Message> senderQueue, Object sender) {
        GetStatusMessage getStatusMessage = new GetStatusMessage(senderQueue, sender);
        sendToMe(getStatusMessage);
    }

    public void stop () {
        for (Node node : getNodes()) {
            node.sendStop(getQueue(), this);
        }
    }

    public void sendNodeStopped (BlockingQueue<Message> senderQueue, Object sender, Node node) {
        NodeStoppedMessage nodeStoppedMessage = new NodeStoppedMessage(senderQueue, sender, node);
        sendToMe(nodeStoppedMessage);
    }

    public void sendNewSession (BlockingQueue<Message> senderQueue, Object sender, Session session) {
        AddSessionMessage addSessionMessage = new AddSessionMessage(senderQueue, sender, session);
        sendToMe(addSessionMessage);
    }

    public void sendSessionsExpiredMessage (BlockingQueue<Message> senderQueue, Object sender, List<Session> expiredSessions) {
        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(senderQueue, sender, expiredSessions);
        sendToMe(sessionsExpiredMessage);
    }

    public void broadcast (WireMessage wireMessage) {
        for (Node node : getNodes()) {
            node.sendSendNetworkMessage (getQueue(), this, wireMessage);
        }
    }

    public void sendNewUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        NewUserMessage newUserMessage = new NewUserMessage(senderQueue, sender, user);
        sendToMe(newUserMessage);
    }

    public void sendUpdateUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        UpdateUserMessage updateUserMessage = new UpdateUserMessage(senderQueue, sender, user);
        sendToMe(updateUserMessage);
    }

    public void sendDeleteUserMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage(senderQueue, sender, name);
        sendToMe(deleteUserMessage);
    }

    public void sendNewTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Topic topic) {
        NewTopicMessage newTopicMessage = new NewTopicMessage(senderQueue, sender, topic);
        sendToMe(newTopicMessage);
    }
}
