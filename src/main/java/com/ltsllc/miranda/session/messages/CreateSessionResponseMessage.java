package com.ltsllc.miranda.session.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class CreateSessionResponseMessage extends Message {
    private Results result;
    private Session session;

    public Session getSession() {
        return session;
    }

    public Results getResult() {
        return result;
    }

    public CreateSessionResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result, Session session) {
        super(Subjects.CreateSessionResponse, senderQueue, sender);

        this.result = result;
        this.session = session;
    }
}
