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

package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.cluster.networkMessages.*;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GetVersionsMessage;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.Conversation;
import com.ltsllc.miranda.node.ConversationMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.EndConversationMessage;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.messages.StartConversationMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.node.networkMessages.*;
import com.ltsllc.miranda.operations.syncfiles.messages.GetFileWireMessage;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.topics.TopicManager;
import org.apache.log4j.Logger;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clark on 1/29/2017.
 */
public class NodeReadyState extends NodeState {
    private static Logger logger = Logger.getLogger(NodeReadyState.class);

    private Map<String, Version> versions = new HashMap<String, Version>();
    private Map<String, Conversation> conversations;

    public NodeReadyState(Node node, Network network) {
        super(node, network);

        this.conversations = new HashMap<String, Conversation>();
    }

    public Map<String, Conversation> getConversations() {
        return conversations;
    }

    public Map<String, Version> getVersions() {
        return versions;
    }


    public State processNetworkMessage(NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case GetVersions: {
                GetVersionsWireMessage getVersionsWireMessage = (GetVersionsWireMessage) networkMessage.getWireMessage();
                nextState = processGetVersionsWireMessage(getVersionsWireMessage);
                break;
            }

            case Versions: {
                VersionsWireMessage versionsWireMessage = (VersionsWireMessage) networkMessage.getWireMessage();
                nextState = processVersionsWireMessage(versionsWireMessage);
                break;
            }

            case GetFile: {
                GetFileWireMessage getFileWireMessage = (GetFileWireMessage) networkMessage.getWireMessage();
                nextState = processGetFileWireMessage(getFileWireMessage);
                break;
            }

            case NewSession: {
                NewSessionWireMessage newSessionWireMessage = (NewSessionWireMessage) networkMessage.getWireMessage();
                nextState = processNewSessionWireMessage(newSessionWireMessage);
                break;
            }

            case ExpiredSessions: {
                SessionsExpiredWireMessage sessionsExpiredWireMessage = (SessionsExpiredWireMessage) networkMessage.getWireMessage();
                nextState = processSessionsExpiredWireMessage(sessionsExpiredWireMessage);
                break;
            }

            case NewUser: {
                NewUserWireMessage newUserWireMessage = (NewUserWireMessage) networkMessage.getWireMessage();
                nextState = processNewUserWireMessage(newUserWireMessage);
                break;
            }

            case UpdateUser: {
                UpdateUserWireMessage updateUserWireMessage = (UpdateUserWireMessage) networkMessage.getWireMessage();
                nextState = processUpdateUserWireMessage(updateUserWireMessage);
                break;
            }

            case DeleteUser: {
                DeleteUserWireMessage deleteUserWireMessage = (DeleteUserWireMessage) networkMessage.getWireMessage();
                nextState = processDeleteUserWireMessage(deleteUserWireMessage);
                break;
            }

            case NewTopic: {
                NewTopicWireMessage newTopicWireMessage = (NewTopicWireMessage) networkMessage.getWireMessage();
                nextState = processNewTopicWireMessage(newTopicWireMessage);
                break;
            }


            default: {
                nextState = super.processNetworkMessage(networkMessage);
                break;
            }
        }

        return nextState;
    }


    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        processConversations(message);

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage(versionMessage);
                break;
            }

            case ConnectionClosed: {
                nextState = StopState.getInstance();
                break;
            }

           case Versions: {
                VersionsMessage versionsMessage = (VersionsMessage) message;
                nextState = processVersionsMessage(versionsMessage);
                break;
            }

            case GetFileResponse: {
                GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                nextState = processGetFileResponseMessage(getFileResponseMessage);
                break;
            }

            case SendNetworkMessage: {
                SendNetworkMessage sendNetworkMessage = (SendNetworkMessage) message;
                nextState = processSendNetworkMessage(sendNetworkMessage);
                break;
            }

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            case StartConversation: {
                StartConversationMessage startConversationMessage = (StartConversationMessage) message;
                nextState = processStartConversationMessage(startConversationMessage);
                break;
            }

            case EndConversation: {
                EndConversationMessage endConversationMessage = (EndConversationMessage) message;
                nextState = processEndConversation(endConversationMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    public void processConversations(Object object) {
        if (!(object instanceof ConversationMessage))
            return;

        ConversationMessage conversationMessage = (ConversationMessage) object;
        Conversation conversation = getConversations().get(conversationMessage.getKey());
        if (null != conversation)
            conversation.forwardMessage(conversationMessage);
    }

    private State processVersionMessage(VersionMessage versionMessage) {
        getVersions().put(versionMessage.getNameVersion().getName(), versionMessage.getNameVersion().getVersion());

        if (versions.size() >= 1) {
            VersionsWireMessage versionsWireMessage = new VersionsWireMessage(versionsToList(versionMessage.getNameVersion()));
            sendOnWire(versionsWireMessage);
        }

        return this;
    }


    private State processGetVersionsWireMessage(GetVersionsWireMessage getVersionsWireMessage) {
        GetVersionResponseWireMessage getVersionsResponseWireMessage = new GetVersionResponseWireMessage();
        Miranda miranda = Miranda.getInstance();

        try {
            getVersionsResponseWireMessage.setVersionFor(Files.Topic, miranda.getTopicManager().getVersion());
            getVersionsResponseWireMessage.setVersionFor(Files.DeliveriesList, miranda.getDeliveryManager().getVersion());
            getVersionsResponseWireMessage.setVersionFor(Files.Cluster, miranda.getCluster().getVersion());
            getVersionsResponseWireMessage.setVersionFor(Files.DeliveriesList, miranda.getDeliveryManager().getVersion());
            getVersionsResponseWireMessage.setVersionFor(Files.Subscription, miranda.getSubscriptionManager().getVersion());
            getVersionsResponseWireMessage.setVersionFor(Files.User, miranda.getUserManager().getVersion());
        } catch (GeneralSecurityException e) {
            getVersionsResponseWireMessage.setError(true);
        }
        return this;
    }


    private List<NameVersion> versionsToList(NameVersion nameVersion) {
        List<NameVersion> list = new ArrayList<NameVersion>();

        list.add(nameVersion);

        return list;
    }


    public State processVersionsWireMessage(VersionsWireMessage versionsWireMessage) {
        VersionsMessage versionsMessage = new VersionsMessage(getNode().getQueue(), this, versionsWireMessage.getVersions());

        Consumer.staticSend(versionsMessage, Miranda.getInstance().getCluster().getQueue());

        return this;
    }

    private State processGetClusterFileMessage(GetClusterFileMessage getClusterFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage(Files.Cluster);
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetFileWireMessage(GetFileWireMessage getFileWireMessage) {
        State nextState = getNode().getCurrentState();

        SingleFile singleFile = getSingleFile(getFileWireMessage);
        Files category = getFileWireMessage.getFile();
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(category,
                singleFile.getVersion(), singleFile.getBytes());
        sendOnNetwork(getFileResponseWireMessage);

        return nextState;
     }


    public SingleFile getSingleFile(GetFileWireMessage getFileWireMessage) {
        SingleFile singleFile = null;

        Miranda miranda = Miranda.getInstance();
        switch (getFileWireMessage.getFile())
        {
            case EventFile:
                singleFile = miranda.getEventManager().getEventsFile(getFileWireMessage.getFileName());
                break;

            case EventList:
                singleFile = miranda.getEventManager().getDirectory().asFile();
                break;

            case Subscription:
                singleFile = miranda.getSubscriptionManager().getSubscriptionsFile();
                break;

            case Topic:
                singleFile = miranda.getTopicManager().getTopicsFile();
                break;

            case User:
                singleFile = miranda.getUserManager().getUsersFile();
                break;

            case Cluster:
                singleFile = miranda.getCluster().getClusterFile();
                break;

            default:
                logger.error("Impossible case: GetFile message has unrecognized file");
                break;
        }

        return singleFile;
    }

     public SingleFile getSingleFile;

     public State processGetFile () {
        State nextState = getNode().getCurrentState();

        Cluster cluster = Miranda.getInstance().getCluster();
        SingleFile singleFile = cluster.getFile();
        byte[] data = singleFile.getBytes();

        return nextState;
     }

    private State processVersionsMessage(VersionsMessage versionsMessage) {
        State nextState = this;

        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(versionsMessage.getVersions());
        sendOnWire(versionsWireMessage);

        return nextState;
    }


    public State processNewTopicWireMessage (NewTopicWireMessage newTopicWireMessage) {
        TopicManager topicManager = Miranda.getInstance().getTopicManager();
        topicManager.sendCreateTopicMessage(getNode().getQueue(), getNode(), newTopicWireMessage.getTopic());

        return getNode().getCurrentState();
    }


    public State processStopMessage(StopMessage stopMessage) throws MirandaException {
        StopWireMessage stopWireMessage = new StopWireMessage();
        getNetwork().sendNetworkMessage(getNode().getQueue(), this, getNode().getHandle(), stopWireMessage);

        return new NodeStoppingState(getNode());
    }

    public State processNewSessionWireMessage(NewSessionWireMessage newSessionWireMessage) {
        Miranda.getInstance().sendAddSessionMessage(getNode().getQueue(), this, newSessionWireMessage.getSession());

        return getNode().getCurrentState();
    }

    public State processSendNetworkMessage(SendNetworkMessage sendNetworkMessage) {
        getNode().sendOnWire(sendNetworkMessage.getWireMessage());

        return getNode().getCurrentState();
    }

    public State processSessionsExpiredWireMessage(SessionsExpiredWireMessage sessionsExpiredWireMessage) {
        Miranda.getInstance().sendSessionsExpiredMessage(getNode().getQueue(), this, sessionsExpiredWireMessage.getExpiredSessions());

        return getNode().getCurrentState();
    }

    public State processNewUserWireMessage(NewUserWireMessage newUserWireMessage) {
        Miranda.getInstance().sendUserAddedMessage(getNode().getQueue(), this, newUserWireMessage.getUserObject().asUser());

        return getNode().getCurrentState();
    }

    public State processUpdateUserWireMessage(UpdateUserWireMessage updateUserWireMessage) {
        Miranda.getInstance().sendUserUpdatedMessage(getNode().getQueue(), this,
                updateUserWireMessage.getUserObject().asUser());

        return getNode().getCurrentState();
    }

    public State processDeleteUserWireMessage(DeleteUserWireMessage deleteUserWireMessage) {
        Miranda.getInstance().sendUserDeletedMessage(getNode().getQueue(), this,
                deleteUserWireMessage.getName());

        return getNode().getCurrentState();
    }

    public State processShutdownMessage(ShutdownMessage shutdownMessage) {
        ShuttingDownWireMessage shuttingDownWireMessage = new ShuttingDownWireMessage();
        sendOnWire(shuttingDownWireMessage);

        getNetwork().sendCloseMessage(getNode().getQueue(), this, getNode().getHandle());

        NodeStoppingState nodeStoppingState = new NodeStoppingState(getNode());
        return nodeStoppingState;
    }

    public State processStartConversationMessage(StartConversationMessage message) {
        Conversation conversation = new Conversation(message.getKey(), message.getRespondTo());
        getConversations().put(message.getKey(), conversation);

        return getNode().getCurrentState();
    }

    public State processEndConversation(EndConversationMessage message) {
        getConversations().remove(message.getKey());

        return getNode().getCurrentState();
    }

    public State processGetFileResponseMessage (GetFileResponseMessage getFileResponseWireMessage){
         GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(getNode().getQueue(),
                 getNode(), getFileResponseWireMessage.getContents());
         return getNode().getCurrentState();
    }
}
