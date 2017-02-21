package com.ltsllc.miranda;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/30/2016.
 */
public class Message implements Perishable {
    private Gson ourGson = new Gson();

    public enum Subjects {
        Ballot,
        ClusterFile,
        ClusterFileChanged,
        ClusterHealthCheck,
        ClusterHealthCheckUpdate,
        Connect,
        Connected,
        ConnectionError,
        ConnectTo,
        ConnectionClosed,
        DropNode,
        DoneSynchronizing,
        Election,
        Expired,
        GarbageCollection,
        GetFile,
        GetSubscriptionsFile,
        GetClusterFile,
        GetFileResponse,
        GetTopicsFile,
        GetUsersFile,
        GetVersions,
        GetVersion,
        HealthCheck,
        HealthCheckUpdate,
        HttpPost,
        Join,
        JoinSuccess,
        Listen,
        Load,
        NewUser,
        NewTopic,
        NewClusterFile,
        NewConnection,
        NetworkMessage,
        NewDelivery,
        NewMessage,
        NewNode,
        NewNodeElement,
        NewSubscription,
        NodeAdded,
        NodesLoaded,
        NodeUpdated,
        RemoteVersion,
        Retry,
        Results,
        ScheduleOnce,
        SchedulePeriodic,
        Starting,
        Synchronize,
        Timeout,
        Version,
        Versions,
        Write,
        WriteSucceeded,
        WriteFailed,
        Error
    }
    private Subjects subject;

    private BlockingQueue<Message> sender;
    private Object senderObject;
    private Exception where;

    public BlockingQueue<Message> getSender () {
        return sender;
    }

    public Subjects getSubject() {
        return subject;
    }

    public Object getSenderObject() { return senderObject; }

    public Exception getWhere() {
        return where;
    }


    public Message (Subjects subject, BlockingQueue<Message> sender, Object senderObject) {
        this.subject = subject;
        this.sender = sender;
        this.senderObject = senderObject;

        this.where = new Exception();
    }

    public void respond (Message m) throws InterruptedException {
        getSender().put(m);
    }

    public boolean expired(long time) {
        return false;
    }


    public String toJson() {
        return ourGson.toJson(this);
    }
}
