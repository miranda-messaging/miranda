/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.session;

import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.session.messages.CheckSessionMessage;
import com.ltsllc.miranda.session.messages.CreateSessionMessage;
import com.ltsllc.miranda.session.messages.GetSessionMessage;
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

    public Session createSession(User user) {
        Long session = null;

        while (null == session || getSessions().containsKey(session)) {
            session = getRandom().nextNonNegativeLong();
        }

        long now = System.currentTimeMillis();

        Session newSession = new Session(user, now + getSessionLength(), session.longValue());

        logger.info("Created session " + newSession.getId() + " for " + user.getName());

        getSessions().put(newSession.getId(), newSession);
        getUserToSession().put(user.getName(), newSession);

        return newSession;
    }

    public boolean isSessionValid(long id) {
        Session session = getSessions().get(id);

        if (null == session)
            return false;

        long now = System.currentTimeMillis();
        return session.getExpires() < now;
    }

    public void updateSession(long id) throws UnknownSession {
        Session session = sessions.get(id);
        if (null == session)
            throw new UnknownSession(id);

        long now = System.currentTimeMillis();
        session.setExpires(now + getSessionLength());
    }

    public void performGarbageCollection() {
        long now = System.currentTimeMillis();

        List<Session> expired = new ArrayList<Session>();

        for (Long key : getSessions().keySet()) {
            Session session = getSessions().get(key);
            if (now >= session.getExpires()) {
                expired.add(session);
            }
        }

        if (expired.size() > 0) {
            for (Session session : expired) {
                getSessions().put(session.getId(), null);
                getUserToSession().put(session.getUser().getName(), null);
            }

            Miranda miranda = Miranda.getInstance();
            Cluster cluster = miranda.getCluster();
            cluster.sendSessionsExpiredMessage(getQueue(), this, expired);
        }
    }

    public void addSession(Session session) throws UnknownSession {
        Session oldSession = getSessions().get(session.getId());

        if (null != oldSession) {
            updateSession(oldSession.getId());
        } else {
            getSessions().put(session.getId(), session);
        }
    }

    public void sendAddSessionMessage(BlockingQueue<Message> senderQueue, Object sender, Session session) {
        AddSessionMessage addSessionMessage = new AddSessionMessage(senderQueue, sender, session);
        sendToMe(addSessionMessage);
    }

    public void sendSessionsExpiredMessage(BlockingQueue<Message> senderQueue, Object sender, List<Session> expiredSessions) {
        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(senderQueue, sender, expiredSessions);
        sendToMe(sessionsExpiredMessage);
    }

    public void expireSessions(List<Session> expiredSessions) {
        for (Session session : expiredSessions) {
            logger.info("Expiring session " + session.getId());
            getSessions().put(session.getId(), null);
        }
    }

    public void sendCreateSession(BlockingQueue<Message> senderQueue, Object sender, User user) {
        CreateSessionMessage createSessionMessage = new CreateSessionMessage(senderQueue, sender, user);
        sendToMe(createSessionMessage);
    }

    public void sendGetSessionMessage(BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetSessionMessage getSessionMessage = new GetSessionMessage(senderQueue, sender, name);
        sendToMe(getSessionMessage);
    }

    public Session getSessionFor(String name) {
        return getUserToSession().get(name);
    }

    public void sendCheckSessionMessage(BlockingQueue<Message> senderQueue, Object sender, long sessionId) {
        CheckSessionMessage checkSessionMessage = new CheckSessionMessage(senderQueue, sender, sessionId);
        sendToMe(checkSessionMessage);
    }

    public Session checkSession(long sessionId) {
        Session session = getSessions().get(sessionId);

        if (session != null) {
            long sessionLength = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_SESSION_LENGTH,
                    MirandaProperties.DEFAULT_SESSION_LENGTH);

            session.extendExpires(sessionLength);
        }

        return session;
    }
}
