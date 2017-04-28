package com.ltsllc.miranda.session.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.user.User;

import javax.xml.transform.Result;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/27/2017.
 */
public class CheckSessionResponseMessage extends Message {
    private Session session;
    private Results result;

    public Results getResult() {
        return result;
    }

    public Session getSession() {
        return session;
    }

    public CheckSessionResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result, Session session) {
        super(Subjects.CheckSessionResponse, senderQueue, sender);

        this.result = result;
        this.session = session;
    }
}
