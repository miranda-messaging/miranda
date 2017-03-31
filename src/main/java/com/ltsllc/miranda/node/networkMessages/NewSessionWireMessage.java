package com.ltsllc.miranda.node.networkMessages;

import com.ltsllc.miranda.session.Session;

/**
 * Created by Clark on 3/30/2017.
 */
public class NewSessionWireMessage extends WireMessage {
    private Session session;

    public Session getSession() {
        return session;
    }

    public NewSessionWireMessage (Session session) {
        super(WireSubjects.NewSession);
        this.session = session;
    }

    public boolean equals (Object o) {
        if (null == o || !(o instanceof NewSessionWireMessage))
            return false;

        NewSessionWireMessage other = (NewSessionWireMessage) o;
        return getSession().equals(other.getSession());
    }
}
