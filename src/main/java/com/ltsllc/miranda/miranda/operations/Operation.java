package com.ltsllc.miranda.miranda.operations;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class Operation extends Consumer {
    private BlockingQueue<Message> requester;
    private Session session;

    public Session getSession() {
        return session;
    }

    public BlockingQueue<Message> getRequester() {
        return requester;
    }

    public Operation (String name, BlockingQueue<Message> requester, Session session) {
        super(name);

        this.requester = requester;
        this.session = session;
    }
}
