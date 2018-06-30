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

package com.ltsllc.miranda.cluster.states;

import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.clientinterface.objects.ClusterStatusObject;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.*;
import com.ltsllc.miranda.cluster.networkMessages.*;

import com.ltsllc.miranda.manager.states.ManagerReadyState;
import com.ltsllc.miranda.message.LoadResponseMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.EndConversationMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.StartConversationMessage;
import com.ltsllc.miranda.node.networkMessages.NewSessionWireMessage;
import com.ltsllc.miranda.node.networkMessages.SessionsExpiredWireMessage;
import com.ltsllc.miranda.servlet.status.GetStatusMessage;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionMessage;
import com.ltsllc.miranda.topics.messages.CreateTopicMessage;
import com.ltsllc.miranda.topics.messages.DeleteTopicMessage;
import com.ltsllc.miranda.topics.messages.UpdateTopicMessage;
import com.ltsllc.miranda.user.messages.DeleteUserMessage;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.user.messages.UpdateUserMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/3/2017.
 */

/**
 * The cluster is ready to accept commands
 */
public class ClusterReadyState extends ManagerReadyState {
    private static Logger logger = Logger.getLogger(ClusterReadyState.class);

    private Cluster cluster;

    public ClusterReadyState(Cluster cluster) throws MirandaException {
        super(cluster);

        this.cluster = cluster;

        assert (null != this.cluster);
    }

    public Cluster getCluster() {
        return cluster;
    }

    public State processMessage(Message m) throws MirandaException {
        State nextState = this;

        switch (m.getSubject()) {
            case Load: {
                LoadMessage loadMessage = (LoadMessage) m;
                nextState = processLoad(loadMessage);
                break;
            }

            case LoadResponse: {
                LoadResponseMessage loadResponseMessage = (LoadResponseMessage) m;
                nextState = processLoadResponseMessage(loadResponseMessage);
                break;
            }

            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) m;
                nextState = processConnectMessage(connectMessage);
                break;
            }

            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) m;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case ClusterFileChanged: {
                ClusterFileChangedMessage clusterFileChangedMessage = (ClusterFileChangedMessage) m;
                nextState = processClusterFileChangedMessage(clusterFileChangedMessage);
                break;
            }

            case HealthCheck: {
                HealthCheckMessage healthCheckMessage = (HealthCheckMessage) m;
                nextState = processHealthCheck(healthCheckMessage);
                break;
            }

            case DropNode: {
                DropNodeMessage dropNodeMessage = (DropNodeMessage) m;
                nextState = processDropNodeMessage(dropNodeMessage);
                break;
            }

            case GetStatus: {
                GetStatusMessage getStatusMessage = (GetStatusMessage) m;
                nextState = processGetStatusMessage(getStatusMessage);
                break;
            }

            case NewNode: {
                NewNodeMessage newNodeMessage = (NewNodeMessage) m;
                nextState = processNewNodeMessage(newNodeMessage);
                break;
            }

            case AddSession: {
                AddSessionMessage addSessionMessage = (AddSessionMessage) m;
                nextState = processAddSessionMessage(addSessionMessage);
                break;
            }

            case SessionsExpired: {
                SessionsExpiredMessage sessionsExpiredMessage = (SessionsExpiredMessage) m;
                nextState = processSessionsExpiredMessage(sessionsExpiredMessage);
                break;
            }

            case NewUser: {
                NewUserMessage newUserMessage = (NewUserMessage) m;
                nextState = processNewUserMessage(newUserMessage);
                break;
            }

            case UpdateUser: {
                UpdateUserMessage updateUserMessage = (UpdateUserMessage) m;
                nextState = processUpdateUserMessage(updateUserMessage);
                break;
            }

            case DeleteUser: {
                DeleteUserMessage deleteUserMessage = (DeleteUserMessage) m;
                nextState = processDeleteUserMessage(deleteUserMessage);
                break;
            }

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) m;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            case CreateSubscription: {
                CreateSubscriptionMessage createSubscriptionMessage = (CreateSubscriptionMessage) m;
                nextState = processCreateSubscriptionMessage(createSubscriptionMessage);
                break;
            }

            case UpdateSubscription: {
                UpdateSubscriptionMessage updateSubscriptionMessage = (UpdateSubscriptionMessage) m;
                nextState = processUpdateSubscriptionMessage(updateSubscriptionMessage);
                break;
            }

            case DeleteSubscription: {
                DeleteSubscriptionMessage deleteSubscriptionMessage = (DeleteSubscriptionMessage) m;
                nextState = processDeleteSubscriptionMessage(deleteSubscriptionMessage);
                break;
            }

            case UpdateTopic: {
                UpdateTopicMessage updateTopicMessage = (UpdateTopicMessage) m;
                nextState = processUpdateTopicMessage(updateTopicMessage);
                break;
            }

            case CreateTopic: {
                CreateTopicMessage createTopicMessage = (CreateTopicMessage) m;
                nextState = processCreateTopicMessage(createTopicMessage);
                break;
            }

            case DeleteTopic: {
                DeleteTopicMessage deleteTopicMessage = (DeleteTopicMessage) m;
                nextState = processDeleteTopicMessage(deleteTopicMessage);
                break;
            }

            case StartConversation: {
                StartConversationMessage startConversationMessage = (StartConversationMessage) m;
                nextState = processStartConversationMessage(startConversationMessage);
                break;
            }

            case EndConversation: {
                EndConversationMessage endConversationMessage = (EndConversationMessage) m;
                nextState = processEndConversationMessage(endConversationMessage);
                break;
            }

            default:
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }


    private State processLoad(LoadMessage loadMessage) {
        getCluster().load();

        return this;
    }


    private State processNodesLoaded(NodesLoadedMessage nodesLoadedMessage) throws MirandaException {
        getCluster().merge(nodesLoadedMessage.getNodes());

        return this;
    }

    /**
     * Tell the nodes in the cluster to connect.
     */
    private State processConnectMessage(ConnectMessage connectMessage) {
        getCluster().connect();

        return this;
    }


    /**
     * This is called when the cluster file has one or more new nodes.
     *
     * @param clusterFileChangedMessage
     * @return
     */
    private State processClusterFileChangedMessage(ClusterFileChangedMessage clusterFileChangedMessage) throws MirandaException {
        getCluster().merge(clusterFileChangedMessage.getFile());

        return this;
    }

    private State processHealthCheck(HealthCheckMessage healthCheckMessage) {
        getCluster().performHealthCheck();

        return this;
    }


    private State processGetVersionMessage(GetVersionMessage getVersionMessage) {
        GetVersionMessage getVersionMessage2 = new GetVersionMessage(getCluster().getQueue(), this, getVersionMessage.getRequester());
        send(getCluster().getFile().getQueue(), getVersionMessage2);

        return this;
    }

    /**
     * A {@link NodeElement} has "timed out" and been dropped from the cluster file.
     *
     * @param dropNodeMessage
     * @return
     */
    private State processDropNodeMessage(DropNodeMessage dropNodeMessage) {
        Node node = getCluster().matchingNode(dropNodeMessage.getDroppedNode());

        if (null != node) {
            if (node.isConnected()) {
                logger.warn("Asked to drop a connected node (" + dropNodeMessage.getDroppedNode() + "), ignoring");
            } else {
                logger.info("Dropping node from cluster: " + dropNodeMessage.getDroppedNode());
                getCluster().getNodes().remove(node);
            }
        }

        return this;
    }


    private State processGetStatusMessage(GetStatusMessage getStatusMessage) throws MirandaException {
        ClusterStatusObject clusterStatusObject = getCluster().getStatus();
        GetStatusResponseMessage response = new GetStatusResponseMessage(getCluster().getQueue(), this, clusterStatusObject);
        getStatusMessage.reply(response);

        return this;
    }

    private State processNewNodeMessage(NewNodeMessage newNodeMessage) {
        getCluster().getNodes().add(newNodeMessage.getNode());

        return this;
    }

    private State processLoadResponseMessage(LoadResponseMessage loadResponseMessage) throws MirandaException {
        getCluster().merge(loadResponseMessage.getData());

        return this;
    }

    public State processAddSessionMessage(AddSessionMessage addSessionMessage) {
        NewSessionWireMessage newSessionWireMessage = new NewSessionWireMessage(addSessionMessage.getSession());
        getCluster().broadcast(newSessionWireMessage);

        return getCluster().getCurrentState();
    }

    public State processSessionsExpiredMessage(SessionsExpiredMessage sessionsExpiredMessage) {
        SessionsExpiredWireMessage sessionsExpiredWireMessage = new SessionsExpiredWireMessage(sessionsExpiredMessage.getExpiredSessions());
        getCluster().broadcast(sessionsExpiredWireMessage);

        return getCluster().getCurrentState();
    }

    public State processNewUserMessage(NewUserMessage newUserMessage) {
        try {
            NewUserWireMessage newUserWireMessage = new NewUserWireMessage(newUserMessage.getUser().asUserObject());
            getCluster().broadcast(newUserWireMessage);

            return getCluster().getCurrentState();
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }
    }

    public State processUpdateUserMessage(UpdateUserMessage updateUserMessage) {
        try {
            UserObject userObject = updateUserMessage.getUser().asUserObject();

            UpdateUserWireMessage updateUserWireMessage = new UpdateUserWireMessage(userObject);
            getCluster().broadcast(updateUserWireMessage);

            return getCluster().getCurrentState();
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }
    }

    public State processDeleteUserMessage(DeleteUserMessage deleteUserMessage) {
        DeleteUserWireMessage deleteUserWireMessage = new DeleteUserWireMessage(deleteUserMessage.getName());
        getCluster().broadcast(deleteUserWireMessage);

        return getCluster().getCurrentState();
    }

    public State processShutdownMessage(ShutdownMessage shutdownMessage) {
        ClusterShutdownState clusterShutdownState = new ClusterShutdownState(shutdownMessage.getSender());
        return clusterShutdownState;
    }

    public State processCreateSubscriptionMessage(CreateSubscriptionMessage createSubscriptionMessage) {
        NewSubscriptionWireMessage newSubscriptionWireMessage = new NewSubscriptionWireMessage(createSubscriptionMessage.getSubscription());
        getCluster().broadcast(newSubscriptionWireMessage);

        return getCluster().getCurrentState();
    }

    public State processUpdateSubscriptionMessage(UpdateSubscriptionMessage updateSubscriptionMessage) {
        UpdateSubscriptionWireMessage updateSubscriptionWireMessage = new UpdateSubscriptionWireMessage(updateSubscriptionMessage.getSubscription());
        getCluster().broadcast(updateSubscriptionWireMessage);

        return getCluster().getCurrentState();
    }

    public State processDeleteSubscriptionMessage(DeleteSubscriptionMessage deleteSubscriptionMessage) {
        DeleteSubscriptionWireMessage deleteSubscriptionWireMessage = new DeleteSubscriptionWireMessage(
                deleteSubscriptionMessage.getSubscriptionName());
        getCluster().broadcast(deleteSubscriptionWireMessage);

        return getCluster().getCurrentState();
    }

    public State processCreateTopicMessage(CreateTopicMessage createTopicMessage) {
        NewTopicWireMessage newTopicWireMessage = new NewTopicWireMessage(createTopicMessage.getTopic());
        getCluster().broadcast(newTopicWireMessage);

        return getCluster().getCurrentState();
    }

    public State processUpdateTopicMessage(UpdateTopicMessage updateTopicMessage) {
        UpdateTopicWireMessage updateTopicWireMessage = new UpdateTopicWireMessage(updateTopicMessage.getTopic());
        getCluster().broadcast(updateTopicWireMessage);

        return getCluster().getCurrentState();
    }

    public State processDeleteTopicMessage(DeleteTopicMessage deleteTopicMessage) {
        DeleteTopicWireMessage deleteTopicWireMessage = new DeleteTopicWireMessage(deleteTopicMessage.getTopicName());
        getCluster().broadcast(deleteTopicWireMessage);

        return getCluster().getCurrentState();
    }

    public void forwardMessage(Message message) {
        for (Node node : getCluster().getNodes()) {
            Consumer.staticSend(message, node.getQueue());
        }
    }

    public State processStartConversationMessage(StartConversationMessage message) {
        forwardMessage(message);
        return getCluster().getCurrentState();
    }

    public State processEndConversationMessage(EndConversationMessage message) {
        forwardMessage(message);
        return getCluster().getCurrentState();
    }
}
