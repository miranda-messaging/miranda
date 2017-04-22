package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.RemoteVersionMessage;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.miranda.operations.login.LoginOperation;
import com.ltsllc.miranda.miranda.operations.topic.CreateTopicOperation;
import com.ltsllc.miranda.miranda.operations.user.CreateUserOperation;
import com.ltsllc.miranda.miranda.operations.user.DeleteUserOperation;
import com.ltsllc.miranda.miranda.operations.user.UpdateUserOperation;
import com.ltsllc.miranda.node.messages.*;
import com.ltsllc.miranda.servlet.messages.GetStatusMessage;
import com.ltsllc.miranda.servlet.messages.GetStatusResponseMessage;
import com.ltsllc.miranda.servlet.objects.StatusObject;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.subsciptions.OwnerQueryResponseMessage;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.topics.messages.CreateTopicMessage;
import com.ltsllc.miranda.topics.messages.UpdateTopicResponseMessage;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.user.messages.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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
        GarbageCollectionMessage garbageCollectionMessage2 = new GarbageCollectionMessage(getMiranda().getQueue(), this);

        send(getMiranda().getSubscriptionManager().getQueue(), garbageCollectionMessage2);
        send(getMiranda().getTopicManager().getQueue(), garbageCollectionMessage2);
        send(getMiranda().getUserManager().getQueue(), garbageCollectionMessage2);

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
                deleteUserMessage.getName());

        deleteUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processCreateUserMessage (CreateUserMessage createUserMessage) {
        getMiranda().getUserManager().sendCreateUserMessage(getMiranda().getQueue(), this, createUserMessage.getUser());

        CreateUserOperation createUserOperation = new CreateUserOperation(createUserMessage.getSender(),
                createUserMessage.getUser());
        createUserOperation.start();

        return getMiranda().getCurrentState();
    }

    public State processUpdateUserMessage (UpdateUserMessage updateUserMessage) {
        getMiranda().getUserManager().sendUpdateUserMessage(getMiranda().getQueue(), this, updateUserMessage.getUser());

        UpdateUserOperation updateUserOperation = new UpdateUserOperation(updateUserMessage.getUser(),
                updateUserMessage.getSender());

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
        CreateTopicOperation createTopicOperation = new CreateTopicOperation(createTopicMessage.getTopic(),
                createTopicMessage.getSender());

        createTopicOperation.start();

        return getMiranda().getCurrentState();
    }
}
