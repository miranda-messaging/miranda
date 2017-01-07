package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/30/2016.
 */
public class Message {
    public enum Subjects {
        NewUser,
        NewTopic,
        NewSubscription,
        NewMessage,
        NewDelivery,
        Election,
        Ballot,
        Results,
        Connect,
        Connected,
        Write,
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

    public Message (Subjects subject) {
        mySubject = subject;
    }

    public void respond (Message m) throws InterruptedException {
        getSender().put(m);
    }
}
