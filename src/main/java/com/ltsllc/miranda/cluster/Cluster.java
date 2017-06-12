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

package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.cluster.messages.NewNodeMessage;
import com.ltsllc.miranda.cluster.messages.NodeStoppedMessage;
import com.ltsllc.miranda.cluster.messages.NodesUpdatedMessage;
import com.ltsllc.miranda.cluster.states.ClusterStartState;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.manager.Manager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.servlet.cluster.ClusterStatusObject;
import com.ltsllc.miranda.servlet.status.GetStatusMessage;
import com.ltsllc.miranda.servlet.status.NodeStatus;
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
import org.apache.log4j.Logger;

import java.io.IOException;
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
public class Cluster extends Manager<Node, NodeElement> {
    public static final String NAME = "cluster";

    private Logger logger = Logger.getLogger(Cluster.class);

    private Network network;
    private boolean clusterFileResponded;

    public boolean getClusterFileResponded() {
        return clusterFileResponded;
    }

    public void setClusterFileResponded(boolean clusterFileResponded) {
        this.clusterFileResponded = clusterFileResponded;
    }


    public Cluster(Network network, String filename) throws IOException {
        super(NAME, filename);

        this.network = network;
    }

    public Cluster(Network network, boolean testMode) {
        super(NAME, testMode);

        this.network = network;
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

    public SingleFile<NodeElement> createFile (String filename) throws IOException {
        return new ClusterFile(filename, Miranda.getInstance().getReader(), Miranda.getInstance().getWriter(), getQueue());
    }

    public State createStartState () {
        return new ClusterStartState(this);
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

    public void sendEventCreatedMessage (BlockingQueue<Message> senderQueue, Object sender, Event event) {
        EventCreatedMessage eventCreatedMessage = new EventCreatedMessage (senderQueue, sender, event);
        sendToMe(eventCreatedMessage);
    }
}
