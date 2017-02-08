package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/30/2016.
 */
public class Message {
    public enum Subjects {
        Ballot,
        Connect,
        Connected,
        ConnectionError,
        ConnectTo,
        ConnectionClosed,
        GetVersion,
        Join,
        JoinSuccess,
        Listen,
        Load,
        NewUser,
        NewTopic,
        NetworkMessage,
        NewSubscription,
        NewDelivery,
        NewMessage,
        NewNode,
        NodeAdded,
        NodesLoaded,
        Election,
        Results,
        Schedule,
        Starting,
        Timeout,
        Version,
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
