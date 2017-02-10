package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/30/2016.
 */
public class Message {
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
        GetClusterFile,
        GetVersion,
        HealthCheck,
        HealthCheckUpdate,
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
        NewSubscription,
        NodeAdded,
        NodesLoaded,
        NodeUpdated,
        Election,
        Results,
        Schedule,
        Starting,
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
}
