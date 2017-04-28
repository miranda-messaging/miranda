package com.ltsllc.miranda.session.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.Message.Subjects.CheckSession;

/**
 * Created by Clark on 4/27/2017.
 */
public class CheckSessionMessage extends Message {
    private long sessionId;

    public long getSessionId() {
        return sessionId;
    }

    public CheckSessionMessage (BlockingQueue<Message> senderQueue, Object sender, long sessionId) {
        super(CheckSession, senderQueue, sender);

        this.sessionId = sessionId;
    }
}
