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
        Join,
        JoinSuccess,
        Listen,
        Load,
        NewUser,
        NewTopic,
        NetworkMessage,
        NewSubscription,
        NewMessage,
        NewDelivery,
        NodesLoaded,
        Election,
        Results,
        Schedule,
        Starting,
        Timeout,
        Write,
        WriteSucceeded,
        WriteFailed,
        Error
    }
    private Subjects mySubject;

    private BlockingQueue<Message> sender;

    public BlockingQueue<Message> getSender () {
        return sender;
    }

    public Subjects getSubject() {
        return mySubject;
    }

    public Message (Subjects subject, BlockingQueue<Message> sender) {
        mySubject = subject;
        this.sender = sender;
    }

    public void respond (Message m) throws InterruptedException {
        getSender().put(m);
    }
}
