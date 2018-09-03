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

package com.ltsllc.miranda.message;

import com.google.gson.Gson;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/30/2016.
 */
public class Message implements Cloneable {
    public enum Subjects {
        Auction,
        AuctionAbort,
        AuctionReply,
        AuctionResults,
        AuctionTimeout,
        AddObjects,
        AddServlet,
        AddServletResponse,
        AddServlets,
        AddSession,
        AttemptDelivery,
        Ballot,
        Bid,
        Bootstrap,
        BootstrapResponse,
        Broadcast,
        Cancel,
        CheckSession,
        CheckSessionResponse,
        Close,
        CloseResponse,
        ClusterFile,
        ClusterFileChanged,
        ClusterHealthCheck,
        ClusterHealthCheckUpdate,
        Connect,
        ConnectFailed,
        ConnectSucceeded,
        ConnectionError,
        ConnectTo,
        ConnectionClosed,
        ConnectionCreated,
        Create,
        CreateBid,
        CreateBidResponse,
        CreateEvent,
        CreateEventResponse,
        CreateResponse,
        CreateSession,
        CreateSessionResponse,
        CreateSubscription,
        CreateSubscriptionResponse,
        CreateTopic,
        CreateTopicResponse,
        CreateUser,
        CreateUserResponse,
        DecrementPanicCount,
        DeleteTopic,
        DeleteTopicResponse,
        DeleteUser,
        DeleteUserResponse,
        DeleteSubscription,
        DeleteSubscriptionResponse,
        DeliverEvent,
        DeliveryResult,
        DirectoryScanned,
        Disconnect,
        Disconnected,
        DoneSynchronizing,
        DropNode,
        DuplicateUser,
        Election,
        EndConversation,
        Error,
        Evict,
        ExceptionDuringScanMessage,
        Expired,
        FileChanged,
        FileDoesNotExist,
        FileLoaded,
        FileWritten,
        GarbageCollection,
        GetClusterFile,
        GetDeliveries,
        GetEvent,
        GetEventReply,
        GetFile,
        GetFileResponse,
        GetNodeCount,
        GetNodeCountResponse,
        GetSession,
        GetSessionResponse,
        GetStatus,
        GetStatusResponse,
        GetSubscriptionsFile,
        GetSubcription,
        GetSubscriptionResponse,
        GetSubscriptionsResponse,
        GetSystemMessages,
        GetTopic,
        GetTopicResponse,
        GetTopicsResponse,
        GetTopicsFile,
        GetUser,
        GetUsersResponse,
        GetUserResponse,
        GetUsersFile,
        GetVersions,
        GetVersion,
        HealthCheck,
        HealthCheckUpdate,
        HttpPost,
        Join,
        JoinSuccess,
        List,
        ListResponse,
        ListSubscriptions,
        ListTopics,
        ListUsers,
        ListUsersResponse,
        Listen,
        Load,
        LoadResponse,
        Login,
        LoginResponse,
        NetworkError,
        NewEvent,
        NewEventResponse,
        NewNode,
        NewTopic,
        NewTopicResponse,
        NewClusterFile,
        NewConnection,
        NetworkMessage,
        NetworkConversationMessage,
        NewDelivery,
        NewMessage,
        NewNodeElement,
        NewProperties,
        NewSubscription,
        NewUser,
        NewUserResponse,
        NoConnection,
        NodeAdded,
        NodesLoaded,
        NodeStopped,
        NodesUpdated,
        NodeUpdated,
        Notification,
        OwnerQuery,
        OwnerQueryResponse,
        Panic,
        PublisherMessage,
        RemoveObjects,
        RemoteVersion,
        Read,
        ReadResponse,
        Retry,
        Result,
        Results,
        Scan,
        ScanResponse,
        ScanCompleteMessage,
        ScheduleDelivery,
        ScheduleOnce,
        SchedulePeriodic,
        SessionsExpired,
        SendError,
        SendMessage,
        SendNetworkMessage,
        Shutdown,
        ShutdownResponse,
        SetupServlets,
        Start,
        StartConversation,
        StartHttpServer,
        Starting,
        Stop,
        StopWatching,
        Subscribe,
        Synchronize,
        Timeout,
        UnknownHandle,
        UnrecognizedUser,
        UnwatchFile,
        UpdateObjects,
        UpdateTopic,
        UpdateTopicResponse,
        UpdateUser,
        UpdateUserResponse,
        UpdateSubscription,
        UpdateSubscriptionResponse,
        UserCreated,
        UserAdded,
        UserUpdated,
        UserDeleted,
        Version,
        Versions,
        WatchFile,
        WatchDirectory,
        Write,
        WriteResponse, LocalTopics, LocalSubscriptions,
    }

    private static Gson ourGson = new Gson();
    private static Logger logger = Logger.getLogger(Message.class);

    private Subjects subject;
    private BlockingQueue<Message> sender;
    private Object senderObject;
    private Exception where;


    public void setSender(BlockingQueue<Message> queue) {
        this.sender = queue;
    }

    public void setSenderObject(Object object) {
        this.senderObject = object;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public BlockingQueue<Message> getSender() {
        return sender;
    }

    public Object getSenderObject() {
        return senderObject;
    }

    public Exception getWhere() {
        return where;
    }

    public void setWhere(Exception where) {
        this.where = where;
    }

    public Message () {}

    public Message(Subjects subject, BlockingQueue<Message> senderQueue, Object senderObject) {
        this.subject = subject;
        this.sender = senderQueue;
        this.senderObject = senderObject;

        this.where = new Exception();
    }

    public Message (Message other) {
        setSender(other.getSender());
        setSenderObject(other.getSenderObject());
        setSubject(other.getSubject());
        setWhere(other.getWhere());
    }

    public boolean expired(long time) {
        return false;
    }


    public String toJson() {
        String json = ourGson.toJson(this);
        return json;
    }

    public void reply(Message message) throws MirandaException {
        try {
            logger.debug (message.getSenderObject() + " sent " + message);
            getSender().put(message);
        } catch (InterruptedException e) {
            Panic panic = new Panic("Interrupted trying to send reply.", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }

    public boolean equals(Object o) {
        if (null == o || !(o instanceof Message))
            return false;

        Message other = (Message) o;

        return getSubject().equals(other.getSubject())
                && getSender() == other.getSender()
                && getSenderObject() == other.getSenderObject();
    }

    public String toString () {
        return getClass().getSimpleName();
    }

    public Object clone () {
        Message clone = new Message();
        deepCopy(this, clone);
        return clone;
    }


    public void deepCopy (Message original, Message clone) {
        clone.setSender(original.getSender());
        clone.setSubject(original.getSubject());
        clone.setWhere(new Exception());
        clone.setSenderObject(original.getSenderObject());
    }
}
