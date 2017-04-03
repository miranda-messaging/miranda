package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/30/2017.
 */
public class AddSessionMessage extends Message {
    private Session session;

    public Session getSession() {
        return session;
    }

    public AddSessionMessage(BlockingQueue<Message> senderQueue, Object sender, Session session) {
        super(Subjects.AddSession, senderQueue, sender);

        this.session = session;
    }
}
