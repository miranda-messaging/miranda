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

package com.ltsllc.miranda.miranda.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.ShutdownMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.objects.NodeStatus;
import com.ltsllc.miranda.clientinterface.objects.StatusObject;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.RemoteVersionMessage;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.AuctionMessage;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.messages.GetVersionsMessage;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.*;
import com.ltsllc.miranda.operations.auction.AuctionOperation;
import com.ltsllc.miranda.operations.login.LoginOperation;
import com.ltsllc.miranda.operations.subscriptions.CreateSubscriptionOperation;
import com.ltsllc.miranda.operations.subscriptions.DeleteSubscriptionOperation;
import com.ltsllc.miranda.operations.subscriptions.UpdateSubscriptionOperation;
import com.ltsllc.miranda.operations.topic.CreateTopicOperation;
import com.ltsllc.miranda.operations.topic.DeleteTopicOperation;
import com.ltsllc.miranda.operations.topic.UpdateTopicOperation;
import com.ltsllc.miranda.operations.user.CreateUserOperation;
import com.ltsllc.miranda.operations.user.DeleteUserOperation;
import com.ltsllc.miranda.operations.user.UpdateUserOperation;
import com.ltsllc.miranda.servlet.status.GetStatusMessage;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionMessage;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.topics.messages.CreateTopicMessage;
import com.ltsllc.miranda.topics.messages.DeleteTopicMessage;
import com.ltsllc.miranda.topics.messages.UpdateTopicMessage;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.user.messages.CreateUserMessage;
import com.ltsllc.miranda.user.messages.DeleteUserMessage;
import com.ltsllc.miranda.user.messages.LoginMessage;
import com.ltsllc.miranda.user.messages.UpdateUserMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class ReadyState extends State {
    private static Logger logger = Logger.getLogger(ReadyState.class);

    private Map<String, BlockingQueue<Message>> deleteUserToQueue = new HashMap<String, BlockingQueue<Message>>();
    private Map<String, List<String>> deleteUserToSubsystems = new HashMap<String, List<String>>();

    private Map<String, BlockingQueue<Message>> createUserToQueue = new HashMap<String, BlockingQueue<Message>>();
    private Map<String, BlockingQueue<Message>> updateUserToQueue = new HashMap<String, BlockingQueue<Message>>();

    public static void setLogger(Logger logger) {
        ReadyState.logger = logger;
    }

    public ReadyState(Miranda miranda) throws MirandaException {
        super(miranda);
    }

    public Miranda getMiranda() {
        return (Miranda) getContainer();
    }

    public Map<String, BlockingQueue<Message>> getDeleteUserToQueue() {
        return deleteUserToQueue;
    }

    public Map<String, List<String>> getDeleteUserToSubsystems() {
        return deleteUserToSubsystems;
    }

    public Map<String, BlockingQueue<Message>> getCreateUserToQueue() {
        return createUserToQueue;
    }

    public Map<String, BlockingQueue<Message>> getUpdateUserToQueue() {
        return updateUserToQueue;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case GetVersions: {
                GetVersionsMessage getVersionsMessage = (GetVersionsMessage) message;
                nextState = processGetVersionsMessage(getVersionsMessage);
                break;
            }

            case NewConnection: {
                NewConnectionMessage newConnectionMessage = (NewConnectionMessage) message;
                nextState = processNewConnectionMessage(newConnectionMessage);
                break;
            }

            case Versions: {
                VersionsMessage versionsMessage = (VersionsMessage) message;
                nextState = processVersionsMessage(versionsMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage(versionMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case GetStatus: {
                GetStatusMessage getStatusMessage = (GetStatusMessage) message;
                nextState = processGetStatusMessage(getStatusMessage);
                break;
            }

            case AddSession: {
                AddSessionMessage addSessionMessage = (AddSessionMessage) message;
                nextState = processAddSessionMessage(addSessionMessage);
                break;
            }

            case SessionsExpired: {
                SessionsExpiredMessage sessionsExpiredMessage = (SessionsExpiredMessage) message;
                nextState = processSessionsExpiredMessage(sessionsExpiredMessage);
                break;
            }

            case DeleteUser: {
                DeleteUserMessage deleteUserMessage = (DeleteUserMessage) message;
                nextState = processDeleteUserMessage(deleteUserMessage);
                break;
            }

            case CreateUser: {
                CreateUserMessage createUserMessage = (CreateUserMessage) message;
                nextState = processCreateUserMessage(createUserMessage);
                break;
            }

            case CreateTopic: {
                CreateTopicMessage createTopicMessage = (CreateTopicMessage) message;
                nextState = processCreateTopicMessage(createTopicMessage);
                break;
            }

            case UpdateTopic: {
                UpdateTopicMessage updateTopicMessage = (UpdateTopicMessage) message;
                nextState = processUpdateTopicMessage(updateTopicMessage);
                break;
            }

            case DeleteTopic: {
                DeleteTopicMessage deleteTopicMessage = (DeleteTopicMessage) message;
                nextState = processDeleteTopicMessage(deleteTopicMessage);
                break;
            }

            case UpdateUser: {
                UpdateUserMessage updateUserMessage = (UpdateUserMessage) message;
                nextState = processUpdateUserMessage(updateUserMessage);
                break;
            }

            case UserAdded: {
                UserAddedMessage userAddedMessage = (UserAddedMessage) message;
                nextState = processUserAddedMessage(userAddedMessage);
                break;
            }

            case UserUpdated: {
                UserUpdatedMessage userUpdatedMessage = (UserUpdatedMessage) message;
                nextState = processUserUpdatedMessage(userUpdatedMessage);
                break;
            }

            case UserDeleted: {
                UserDeletedMessage userDeletedMessage = (UserDeletedMessage) message;
                nextState = processUserDeletedMessage(userDeletedMessage);
                break;
            }

            case Login: {
                LoginMessage loginMessage = (LoginMessage) message;
                nextState = processLoginMessage(loginMessage);
                break;
            }

            case CreateSubscription: {
                CreateSubscriptionMessage createSubscriptionMessage = (CreateSubscriptionMessage) message;
                nextState = processCreateSubscriptionMessage(createSubscriptionMessage);
                break;
            }

            case UpdateSubscription: {
                UpdateSubscriptionMessage updateSubscriptionMessage = (UpdateSubscriptionMessage) message;
                nextState = processUpdateSubscriptionMessage(updateSubscriptionMessage);
                break;
            }

            case DeleteSubscription: {
                DeleteSubscriptionMessage deleteSubscriptionMessage = (DeleteSubscriptionMessage) message;
                nextState = processDeleteSubscriptionMessage(deleteSubscriptionMessage);
                break;
            }

            case Auction: {
                AuctionMessage auctionMessage = (AuctionMessage) message;
                nextState = processAuctionMessage(auctionMessage);
                break;
            }

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    private State processNewConnectionMessage(NewConnectionMessage newConnectionMessage) {
        return getMiranda().getCurrentState();
    }


    private State processVersionsMessage(VersionsMessage versionsMessage) {
        for (NameVersion nameVersion : versionsMessage.getVersions()) {
            RemoteVersionMessage remoteVersionMessage = new RemoteVersionMessage(getMiranda().getQueue(), this, versionsMessage.getSender(), nameVersion);

            if (nameVersion.getName().equalsIgnoreCase("cluster")) {
                send(ClusterFile.getInstance().getQueue(), remoteVersionMessage);
            } else if (nameVersion.getName().equalsIgnoreCase("users")) {
                send(UsersFile.getInstance().getQueue(), remoteVersionMessage);
            } else if (nameVersion.getName().equalsIgnoreCase("topics")) {
                send(TopicsFile.getInstance().getQueue(), remoteVersionMessage);
            } else if (nameVersion.getName().equalsIgnoreCase("subscriptions")) {
                send(SubscriptionsFile.getInstance().getQueue(), remoteVersionMessage);
            }
        }

        return this;
    }


    private State processGetVersionsMessage(GetVersionsMessage getVersionsMessage) throws MirandaException {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getMiranda().getQueue(), this, getMiranda().getQueue());

        send(Miranda.getInstance().getCluster().getQueue(), getVersionMessage);
        send(UsersFile.getInstance().getQueue(), getVersionMessage);
        send(TopicsFile.getInstance().getQueue(), getVersionMessage);
        send(SubscriptionsFile.getInstance().getQueue(), getVersionMessage);

        GettingVersionsState gettingVersionsState = new GettingVersionsState(getMiranda(), getVersionsMessage.getSender());
        return gettingVersionsState;
    }


    private State processVersionMessage(VersionMessage versionMessage) {
        logger.error("processVersionMessage called");
        return this;
    }

    private State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        getMiranda().getUserManager().sendGarbageCollectionMessage(getMiranda().getQueue(), this);
        getMiranda().getTopicManager().sendGarbageCollectionMessage(getMiranda().getQueue(), this);
        getMiranda().getSubscriptionManager().sendGarbageCollectionMessage(getMiranda().getQueue(), this);

        return this;
    }

    private State processGetStatusMessage(GetStatusMessage getStatusMessage) throws MirandaException {
        NodeStatus statusObject = getMiranda().getStatusImpl();

        GetStatusResponseMessage response = new GetStatusResponseMessage(getMiranda().getQueue(), this, statusObject);
        getStatusMessage.reply(response);

        return this;
    }

    public State processAddSessionMessage(AddSessionMessage addSessionMessage) {
        getMiranda().getSessionManager().sendAddSessionMessage(getMiranda().getQueue(), this, addSessionMessage.getSession());

        return getMiranda().getCurrentState();
    }

    public State processSessionsExpiredMessage(SessionsExpiredMessage sessionsExpiredMessage) {
        getMiranda().getSessionManager().sendSessionsExpiredMessage(getMiranda().getQueue(), this, sessionsExpiredMessage.getExpiredSessions());

        return getMiranda().getCurrentState();
    }

    public State processDeleteUserMessage(DeleteUserMessage deleteUserMessage) throws MirandaException {
        DeleteUserOperation deleteUserOperation = new DeleteUserOperation(deleteUserMessage.getSender(),
                deleteUserMessage.getSession(), deleteUserMessage.getName());

        deleteUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processCreateUserMessage(CreateUserMessage createUserMessage) throws MirandaException {
        CreateUserOperation createUserOperation = new CreateUserOperation(createUserMessage.getSender(),
                createUserMessage.getSession(), createUserMessage.getUser());
        createUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUpdateUserMessage(UpdateUserMessage updateUserMessage) throws MirandaException {
        UpdateUserOperation updateUserOperation = new UpdateUserOperation(updateUserMessage.getSender(),
                updateUserMessage.getSession(), updateUserMessage.getUser());

        updateUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUserAddedMessage(UserAddedMessage userAddedMessage) {
        getMiranda().getUserManager().sendUserAddedMessage(getMiranda().getQueue(), this, userAddedMessage.getUser());

        return getMiranda().getCurrentState();
    }

    public State processUserUpdatedMessage(UserUpdatedMessage userUpdatedMessage) {
        getMiranda().getUserManager().sendUserUpdatedMessage(getMiranda().getQueue(), this, userUpdatedMessage.getUser());

        return getMiranda().getCurrentState();
    }

    public State processUserDeletedMessage(UserDeletedMessage userDeletedMessage) {
        getMiranda().getUserManager().sendUserDeletedMessage(getMiranda().getQueue(), this, userDeletedMessage.getName());

        return getMiranda().getCurrentState();
    }

    public State processLoginMessage(LoginMessage loginMessage) throws MirandaException {
        LoginOperation loginOperation = new LoginOperation(loginMessage.getName(), loginMessage.getSender());
        loginOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processCreateTopicMessage(CreateTopicMessage createTopicMessage) throws MirandaException {
        CreateTopicOperation createTopicOperation = new CreateTopicOperation(createTopicMessage.getSender(),
                createTopicMessage.getSession(), createTopicMessage.getTopic());

        createTopicOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processCreateSubscriptionMessage(CreateSubscriptionMessage createSubscriptionMessage) throws MirandaException {
        CreateSubscriptionOperation createSubscriptionOperation = new CreateSubscriptionOperation(createSubscriptionMessage.getSender(),
                createSubscriptionMessage.getSession(), createSubscriptionMessage.getSubscription());

        createSubscriptionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUpdateSubscriptionMessage(UpdateSubscriptionMessage updateSubscriptionMessage) throws MirandaException {
        UpdateSubscriptionOperation updateSubscriptionOperation = new UpdateSubscriptionOperation(updateSubscriptionMessage.getSender(),
                updateSubscriptionMessage.getSession(), updateSubscriptionMessage.getSubscription());

        updateSubscriptionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processDeleteSubscriptionMessage(DeleteSubscriptionMessage deleteSubscriptionMessage) throws MirandaException {
        DeleteSubscriptionOperation deleteSubscriptionOperation = new DeleteSubscriptionOperation(deleteSubscriptionMessage.getSender(),
                deleteSubscriptionMessage.getSession(), deleteSubscriptionMessage.getSubscriptionName());

        deleteSubscriptionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processAuctionMessage(AuctionMessage auctionMessage) throws MirandaException {
        AuctionOperation auctionOperation = new AuctionOperation();
        auctionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUpdateTopicMessage(UpdateTopicMessage updateTopicMessage) throws MirandaException {
        UpdateTopicOperation updateTopicOperation = new UpdateTopicOperation(updateTopicMessage.getSender(),
                updateTopicMessage.getSession(), updateTopicMessage.getTopic());

        updateTopicOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processDeleteTopicMessage(DeleteTopicMessage deleteTopicMessage) throws MirandaException {
        DeleteTopicOperation deleteTopicOperation = new DeleteTopicOperation(deleteTopicMessage.getSender(),
                deleteTopicMessage.getSession(), deleteTopicMessage.getTopicName());

        deleteTopicOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processShutdownMessage(ShutdownMessage shutdownMessage) throws MirandaException {
        getMiranda().shutdown();

        ShuttingDownState shuttingDownState = new ShuttingDownState(getMiranda());

        return shuttingDownState;
    }
}
