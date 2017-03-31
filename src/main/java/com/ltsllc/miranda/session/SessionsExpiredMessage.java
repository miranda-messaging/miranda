package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/30/2017.
 */
public class SessionsExpiredMessage extends Message {
    private List<Session> expiredSessions;

    public List<Session> getExpiredSessions() {
        return expiredSessions;
    }

    public SessionsExpiredMessage(BlockingQueue<Message> senderQueue, Object sender, List<Session> expiredSessions) {
        super(Subjects.SessionsExpired, senderQueue, sender);

        this.expiredSessions = expiredSessions;
    }
}
