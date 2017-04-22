package com.ltsllc.miranda.session.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/15/2017.
 */
public class GetSessionResponseMessage extends Message {
    private Session session;
    private Results result;

    public Results getResult() {
        return result;
    }

    public Session getSession() {
        return session;
    }

    public GetSessionResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result,
                                      Session session) {
        super(Subjects.GetSessionResponse, senderQueue, sender);

        this.result = result;
        this.session = session;
    }
}
