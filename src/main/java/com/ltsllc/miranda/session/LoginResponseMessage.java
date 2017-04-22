package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginResponseMessage extends Message {
    private Results result;
    private Session session;

    public Session getSession() {
        return session;
    }

    public Results getResult() {

        return result;
    }

    public LoginResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result, Session session) {
        super(Subjects.LoginResponse, senderQueue, sender);

        this.result = result;
        this.session = session;
    }
}
