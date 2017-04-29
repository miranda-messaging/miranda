package com.ltsllc.miranda;

import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/29/2017.
 */
public class SessionMessage extends Message {
    private Session session;

    public Session getSession() {
        return session;
    }

    public SessionMessage (Subjects subject, BlockingQueue<Message> senderQueue, Object sender, Session session) {
        super(subject, senderQueue, sender);

        this.session = session;
    }
}
