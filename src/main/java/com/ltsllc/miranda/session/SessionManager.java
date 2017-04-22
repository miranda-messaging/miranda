package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.session.messages.CreateSessionMessage;
import com.ltsllc.miranda.session.messages.GetSessionMessage;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.util.ImprovedRandom;
import org.apache.log4j.Logger;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * This class is in charge of all the sessions for the System.
 */
public class SessionManager extends Consumer {
    private static Logger logger = Logger.getLogger(SessionManager.class);

    private ImprovedRandom random;
    private Map<Long, Session> sessions;
    private Map<String, Session> userToSession;
    private long sessionLength;

    public ImprovedRandom getRandom() {
        return random;
    }

    public Map<Long, Session> getSessions() {
        return sessions;
    }

    public long getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(long sessionLength) {
        this.sessionLength = sessionLength;
    }

    public Map<String, Session> getUserToSession() {
        return userToSession;
    }

    public SessionManager() throws MirandaException {
        super("session manager");

        sessions = new HashMap<Long, Session>();
        userToSession = new HashMap<String, Session>();

        SecureRandom secureRandom = new SecureRandom();
        random = new ImprovedRandom(secureRandom);

        sessionLength = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_SESSION_LENGTH);

        SessionManagerReadyState readyState = new SessionManagerReadyState(this);
        setCurrentState(readyState);
    }

    public Session createSession (User user) {
        Long session = null;

        while (null == session || getSessions().containsKey(session)) {
            session = getRandom().nextNonNegativeLong();
        }

        long now = System.currentTimeMillis();

        Session newSession = new Session (user, now + getSessionLength(), session.longValue());

        getSessions().put(newSession.getId(), newSession);
        getUserToSession().put(user.getName(), newSession);

        return newSession;
    }

    public boolean isSessionValid (long id) {
        Session session = getSessions().get(id);

        if (null == session)
            return false;

        long now = System.currentTimeMillis();
        return session.getExpires() < now;
    }

    public void updateSession (long id) throws UnknownSession {
        Session session = sessions.get (id);
        if (null == session)
            throw new UnknownSession(id);

        long now = System.currentTimeMillis();
        session.setExpires (now + getSessionLength());
    }

    public void performGarbageCollection () {
        long now = System.currentTimeMillis();

        List<Session> expired = new ArrayList<Session>();

        for (Long key : getSessions().keySet())
        {
            Session session = getSessions().get(key);
            if (now >= session.getExpires())
            {
                expired.add (session);
            }
        }

        if (expired.size() > 0) {
            for (Session session : expired) {
                getSessions().put (session.getId(), null);
                getUserToSession().put (session.getUser().getName(), null);
            }

            Miranda miranda = Miranda.getInstance();
            Cluster cluster = miranda.getCluster();
            cluster.sendSessionsExpiredMessage(getQueue(), this, expired);
        }
    }

    public void addSession (Session session) {
        try {
            Session oldSession = getSessions().get(session.getId());

            if (null != oldSession) {
                updateSession(oldSession.getId());
            } else {
                getSessions().put(session.getId(), session);
            }
        } catch (MirandaException e) {
            logger.error("Exception trying to add session", e);
        }
    }

    public void sendAddSessionMessage(BlockingQueue<Message> senderQueue, Object sender, Session session) {
        AddSessionMessage addSessionMessage = new AddSessionMessage(senderQueue, sender, session);
        sendToMe(addSessionMessage);
    }

    public void sendSessionsExpiredMessage (BlockingQueue<Message> senderQueue, Object sender, List<Session> expiredSessions) {
        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(senderQueue, sender, expiredSessions);
        sendToMe(sessionsExpiredMessage);
    }

    public void expireSessions (List<Session> expiredSessions) {
        for (Session session : expiredSessions) {
            logger.info ("Expiring session " + session.getId());
            getSessions().put(session.getId(), null);
        }
    }

    public void sendCreateSession (BlockingQueue<Message> senderQueue, Object sender, User user) {
        CreateSessionMessage createSessionMessage = new CreateSessionMessage(senderQueue, sender, user);
        sendToMe(createSessionMessage);
    }

    public void sendGetSessionMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetSessionMessage getSessionMessage = new GetSessionMessage (senderQueue, sender, name);
        sendToMe(getSessionMessage);
    }

    public Session getSessionFor (String name) {
        return getUserToSession().get(name);
    }
}
