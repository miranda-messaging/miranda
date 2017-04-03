package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class CreateSessionResponseMessage extends Message {
    private long session;

    public long getSession() {
        return session;
    }

    public CreateSessionResponseMessage (BlockingQueue<Message> senderQueue, Object sender, long session) {
        super(Subjects.CreateSessionResponse, senderQueue, sender);

        this.session = session;
    }
}
