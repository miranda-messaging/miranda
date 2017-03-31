package com.ltsllc.miranda.node.networkMessages;

import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionsExpiredMessage;

import java.util.List;

/**
 * Created by Clark on 3/30/2017.
 */
public class SessionsExpiredWireMessage extends WireMessage {
    private List<Session> expiredSessions;

    public List<Session> getExpiredSessions() {
        return expiredSessions;
    }

    public SessionsExpiredWireMessage (List<Session> expiredSessions) {
        super(WireSubjects.ExpiredSessions);

        this.expiredSessions = expiredSessions;
    }
}
