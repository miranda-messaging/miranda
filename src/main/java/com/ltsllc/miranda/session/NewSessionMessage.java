package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/30/2017.
 */
public class NewSessionMessage extends Message {
    private Session session;

    public Session getSession() {
        return session;
    }

    public NewSessionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session) {
        super(Subjects.NewSession, senderQueue, sender);

        this.session = session;
    }
}
