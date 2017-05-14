package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.cluster.messages.NewNodeMessage;
import com.ltsllc.miranda.cluster.messages.NodeStoppedMessage;
import com.ltsllc.miranda.cluster.messages.NodesUpdatedMessage;
import com.ltsllc.miranda.cluster.states.ClusterStartState;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.Manager;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.servlet.messages.GetStatusMessage;
import com.ltsllc.miranda.servlet.objects.ClusterStatusObject;
import com.ltsllc.miranda.servlet.objects.NodeStatus;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.subsciptions.Subscription;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionMessage;
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
public class Cluster extends Manager<Node, NodeElement> {
    public static final String NAME = "cluster";

    private Logger logger = Logger.getLogger(Cluster.class);

    private static Cluster ourInstance;

    private Network network;
    private boolean clusterFileResponded;

    public boolean getClusterFileResponded() {
        return clusterFileResponded;
    }

    public void setClusterFileResponded(boolean clusterFileResponded) {
        this.clusterFileResponded = clusterFileResponded;
    }

    public static synchronized void initialize(String filename, Reader reader, Writer writer, Network network) {
        if (null == ourInstance) {
            BlockingQueue<Message> clusterQueue = new LinkedBlockingQueue<Message>();

            ClusterFile.initialize(filename, reader, writer, clusterQueue);

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
        super("cluster", clusterFile);

        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this);
        clusterFile.addSubscriber(getQueue(),fileLoadedMessage);

        ClusterStartState clusterStartState = new ClusterStartState(this);
        setCurrentState(clusterStartState);

        this.network = network;
        clusterFile.start();
    }

    public ClusterFile getClusterFile() {
        return (ClusterFile) getFile();
    }

    public List<Node> getNodes() {
        return (List<Node>) getData();
    }

    public Network getNetwork() {
        return network;
    }

    public boolean contains (NodeElement nodeElement) {
        for (Node node : getNodes()) {
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
            send(nodesUpdatedMessage, getClusterFile().getQueue());
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

        send (message, getFile().getQueue());
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
            send(nodesUpdatedMessage, getFile().getQueue());
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
        super.stop();

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
        UpdateUserMessage updateUserMessage = new UpdateUserMessage(senderQueue, sender, null, user);
        sendToMe(updateUserMessage);
    }

    public void sendDeleteUserMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, String name) {
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage(senderQueue, sender, session, name);
        sendToMe(deleteUserMessage);
    }

    public void sendNewTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Topic topic) {
        NewTopicMessage newTopicMessage = new NewTopicMessage(senderQueue, sender, topic);
        sendToMe(newTopicMessage);
    }

    public void sendCreateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender,
                                               Session session, Subscription subscription) {
        CreateSubscriptionMessage createSubscriptionMessage = new CreateSubscriptionMessage(senderQueue, sender, session,
                subscription);

        sendToMe(createSubscriptionMessage);
    }

    public void sendUpdateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                               Subscription subscription) {
        UpdateSubscriptionMessage updateSubscriptionMessage = new UpdateSubscriptionMessage(senderQueue, sender,
                session, subscription);
        sendToMe(updateSubscriptionMessage);
    }

    public void sendDeleteSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                               String name) {
        DeleteSubscriptionMessage deleteSubscriptionMessage = new DeleteSubscriptionMessage(senderQueue, sender, session,
                name);

        sendToMe(deleteSubscriptionMessage);
    }

    public Node convert (NodeElement nodeElement) {
        Node node = new Node(nodeElement, getNetwork(), this);
        return node;
    }

    public boolean disconnected () {
        for (Node node : getNodes()) {
            if (node.isConnected())
                return false;
        }

        return true;
    }

    public void shutdown () {
        for (Node node : getNodes()) {
            node.sendShutdown(getQueue(), this);
        }
    }
}
