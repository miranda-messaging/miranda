package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.RemoteVersionMessage;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.miranda.operations.auction.AuctionOperation;
import com.ltsllc.miranda.miranda.operations.login.LoginOperation;
import com.ltsllc.miranda.miranda.operations.subscriptions.CreateSubscriptionOperation;
import com.ltsllc.miranda.miranda.operations.subscriptions.DeleteSubscriptionOperation;
import com.ltsllc.miranda.miranda.operations.subscriptions.UpdateSubscriptionOperation;
import com.ltsllc.miranda.miranda.operations.topic.CreateTopicOperation;
import com.ltsllc.miranda.miranda.operations.topic.DeleteTopicOperation;
import com.ltsllc.miranda.miranda.operations.topic.UpdateTopicOperation;
import com.ltsllc.miranda.miranda.operations.user.CreateUserOperation;
import com.ltsllc.miranda.miranda.operations.user.DeleteUserOperation;
import com.ltsllc.miranda.miranda.operations.user.UpdateUserOperation;
import com.ltsllc.miranda.node.messages.*;
import com.ltsllc.miranda.servlet.messages.GetStatusMessage;
import com.ltsllc.miranda.servlet.messages.GetStatusResponseMessage;
import com.ltsllc.miranda.servlet.objects.StatusObject;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionMessage;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.topics.messages.CreateTopicMessage;
import com.ltsllc.miranda.topics.messages.DeleteTopicMessage;
import com.ltsllc.miranda.topics.messages.UpdateTopicMessage;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.user.messages.*;
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

    public static void setLogger (Logger logger) {
        ReadyState.logger = logger;
    }

    public ReadyState (Miranda miranda) {
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
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NewConnection: {
                NewConnectionMessage newConnectionMessage = (NewConnectionMessage) message;
                nextState = processNewConnectionMessage(newConnectionMessage);
                break;
            }

            case GetVersions: {
                GetVersionsMessage getVersionsMessage = (GetVersionsMessage) message;
                nextState = processGetVersionsMessage(getVersionsMessage);
                break;
            }

            case Versions: {
                VersionsMessage versionsMessage = (VersionsMessage) message;
                nextState = processVersionsMessage(versionsMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage (versionMessage);
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
                nextState = processDeleteUserMessage (deleteUserMessage);
                break;
            }

            case CreateUser: {
                CreateUserMessage createUserMessage = (CreateUserMessage) message;
                nextState = processCreateUserMessage (createUserMessage);
                break;
            }

            case CreateTopic: {
                CreateTopicMessage createTopicMessage = (CreateTopicMessage) message;
                nextState = processCreateTopicMessage(createTopicMessage);
                break;
            }

            case UpdateTopic: {
                UpdateTopicMessage updateTopicMessage = (UpdateTopicMessage) message;
                nextState = processUpdateTopicMessage (updateTopicMessage);
                break;
            }

            case DeleteTopic: {
                DeleteTopicMessage deleteTopicMessage = (DeleteTopicMessage) message;
                nextState = processDeleteTopicMessage (deleteTopicMessage);
                break;
            }

            case UpdateUser: {
                UpdateUserMessage updateUserMessage = (UpdateUserMessage) message;
                nextState = processUpdateUserMessage (updateUserMessage);
                break;
            }

            case UserAdded: {
                UserAddedMessage userAddedMessage = (UserAddedMessage) message;
                nextState = processUserAddedMessage (userAddedMessage);
                break;
            }

            case UserUpdated: {
                UserUpdatedMessage userUpdatedMessage = (UserUpdatedMessage) message;
                nextState = processUserUpdatedMessage (userUpdatedMessage);
                break;
            }

            case UserDeleted: {
                UserDeletedMessage userDeletedMessage = (UserDeletedMessage) message;
                nextState = processUserDeletedMessage (userDeletedMessage);
                break;
            }

            case Login: {
                LoginMessage loginMessage = (LoginMessage) message;
                nextState = processLoginMessage(loginMessage);
                break;
            }

            case CreateSubscription: {
                CreateSubscriptionMessage createSubscriptionMessage = (CreateSubscriptionMessage) message;
                nextState = processCreateSubscriptionMessage (createSubscriptionMessage);
                break;
            }

            case UpdateSubscription: {
                UpdateSubscriptionMessage updateSubscriptionMessage = (UpdateSubscriptionMessage) message;
                nextState = processUpdateSubscriptionMessage (updateSubscriptionMessage);
                break;
            }

            case DeleteSubscription: {
                DeleteSubscriptionMessage deleteSubscriptionMessage = (DeleteSubscriptionMessage) message;
                nextState = processDeleteSubscriptionMessage (deleteSubscriptionMessage);
                break;
            }

            case Auction: {
                AuctionMessage auctionMessage = (AuctionMessage) message;
                nextState = processAuctionMessage (auctionMessage);
                break;
            }


            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    private State processNewConnectionMessage (NewConnectionMessage newConnectionMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getMiranda().getQueue(), this, getMiranda().getQueue());
        send(newConnectionMessage.getNode().getQueue(), getVersionMessage);

        return this;
    }


    private State processVersionsMessage (VersionsMessage versionsMessage) {
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


    private State processGetVersionsMessage (GetVersionsMessage getVersionsMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getMiranda().getQueue(), this, getMiranda().getQueue());

        send(Cluster.getInstance().getQueue(), getVersionMessage);
        send(UsersFile.getInstance().getQueue(), getVersionMessage);
        send(TopicsFile.getInstance().getQueue(), getVersionMessage);
        send(SubscriptionsFile.getInstance().getQueue(), getVersionMessage);

        GettingVersionsState gettingVersionsState= new GettingVersionsState(getMiranda(), getVersionsMessage.getSender());
        return gettingVersionsState;
    }


    private State processVersionMessage (VersionMessage versionMessage) {
        logger.error("processVersionMessage called");
        return this;
    }

    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getMiranda().getUserManager().sendGarbageCollectionMessage(getMiranda().getQueue(), this);
        getMiranda().getTopicManager().sendGarbageCollectionMessage(getMiranda().getQueue(), this);
        getMiranda().getSubscriptionManager().sendGarbageCollectionMessage(getMiranda().getQueue(), this);

        return this;
    }

    private State processGetStatusMessage (GetStatusMessage getStatusMessage) {
        StatusObject statusObject = getMiranda().getStatusImpl();

        GetStatusResponseMessage response = new GetStatusResponseMessage(getMiranda().getQueue(), this, statusObject);
        getStatusMessage.reply(response);

        return this;
    }

    public State processAddSessionMessage(AddSessionMessage addSessionMessage) {
        getMiranda().getSessionManager().sendAddSessionMessage(getMiranda().getQueue(), this, addSessionMessage.getSession());

        return getMiranda().getCurrentState();
    }

    public State processSessionsExpiredMessage (SessionsExpiredMessage sessionsExpiredMessage) {
        getMiranda().getSessionManager().sendSessionsExpiredMessage (getMiranda().getQueue(), this, sessionsExpiredMessage.getExpiredSessions());

        return getMiranda().getCurrentState();
    }

    public State processDeleteUserMessage (DeleteUserMessage deleteUserMessage) {
        DeleteUserOperation deleteUserOperation = new DeleteUserOperation(deleteUserMessage.getSender(),
                deleteUserMessage.getSession(), deleteUserMessage.getName());

        deleteUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processCreateUserMessage (CreateUserMessage createUserMessage) {
        CreateUserOperation createUserOperation = new CreateUserOperation(createUserMessage.getSender(),
                createUserMessage.getSession(), createUserMessage.getUser());
        createUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUpdateUserMessage (UpdateUserMessage updateUserMessage) {
        UpdateUserOperation updateUserOperation = new UpdateUserOperation(updateUserMessage.getSender(),
                updateUserMessage.getSession(), updateUserMessage.getUser());

        updateUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUserAddedMessage (UserAddedMessage userAddedMessage) {
        getMiranda().getUserManager().sendUserAddedMessage (getMiranda().getQueue(), this, userAddedMessage.getUser());

        return getMiranda().getCurrentState();
    }

    public State processUserUpdatedMessage (UserUpdatedMessage userUpdatedMessage) {
        getMiranda().getUserManager().sendUserUpdatedMessage (getMiranda().getQueue(), this, userUpdatedMessage.getUser());

        return getMiranda().getCurrentState();
    }

    public State processUserDeletedMessage (UserDeletedMessage userDeletedMessage) {
        getMiranda().getUserManager().sendUserDeletedMessage (getMiranda().getQueue(), this, userDeletedMessage.getName());

        return getMiranda().getCurrentState();
    }

    public State processLoginMessage (LoginMessage loginMessage) {
        LoginOperation loginOperation = new LoginOperation(loginMessage.getName(), loginMessage.getSender());
        loginOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processCreateTopicMessage (CreateTopicMessage createTopicMessage) {
        CreateTopicOperation createTopicOperation = new CreateTopicOperation(createTopicMessage.getSender(),
                createTopicMessage.getSession(), createTopicMessage.getTopic());

        createTopicOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processCreateSubscriptionMessage (CreateSubscriptionMessage createSubscriptionMessage) {
        CreateSubscriptionOperation createSubscriptionOperation = new CreateSubscriptionOperation(createSubscriptionMessage.getSender(),
                createSubscriptionMessage.getSession(), createSubscriptionMessage.getSubscription());

        createSubscriptionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUpdateSubscriptionMessage (UpdateSubscriptionMessage updateSubscriptionMessage) {
        UpdateSubscriptionOperation updateSubscriptionOperation = new UpdateSubscriptionOperation(updateSubscriptionMessage.getSender(),
                updateSubscriptionMessage.getSession(), updateSubscriptionMessage.getSubscription());

        updateSubscriptionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processDeleteSubscriptionMessage (DeleteSubscriptionMessage deleteSubscriptionMessage) {
        DeleteSubscriptionOperation deleteSubscriptionOperation = new DeleteSubscriptionOperation(deleteSubscriptionMessage.getSender(),
                deleteSubscriptionMessage.getSession(), deleteSubscriptionMessage.getName());

        deleteSubscriptionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processAuctionMessage (AuctionMessage auctionMessage) {
        AuctionOperation auctionOperation = new AuctionOperation();
        auctionOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUpdateTopicMessage (UpdateTopicMessage updateTopicMessage) {
        UpdateTopicOperation updateTopicOperation = new UpdateTopicOperation(updateTopicMessage.getSender(),
                updateTopicMessage.getSession(), updateTopicMessage.getTopic());

        updateTopicOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processDeleteTopicMessage (DeleteTopicMessage deleteTopicMessage) {
        DeleteTopicOperation deleteTopicOperation = new DeleteTopicOperation(deleteTopicMessage.getSender(),
                deleteTopicMessage.getSession(), deleteTopicMessage.getTopicName());

        deleteTopicOperation.start();

        return getMiranda().getCurrentState();
    }
}
