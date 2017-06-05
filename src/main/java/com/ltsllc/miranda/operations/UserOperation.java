package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by clarkhobbie on 5/24/17.
 */
public class UserOperation extends Operation {
    private Session session;

    public Session getSession() {
        return session;
    }

    public UserOperation (String name, BlockingQueue<Message> requester, Session session) {
        super (name, requester);

        this.session = session;
    }
}
