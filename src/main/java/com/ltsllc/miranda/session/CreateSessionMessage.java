package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class CreateSessionMessage extends Message {
    private String user;

    public String getUser() {
        return user;
    }

    public CreateSessionMessage (BlockingQueue<Message> senderQueue, Object sender, String user) {
        super(Subjects.CreateSession, senderQueue, sender);

        this.user = user;
    }
}
