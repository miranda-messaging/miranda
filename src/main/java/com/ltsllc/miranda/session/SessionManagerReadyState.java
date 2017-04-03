package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;

/**
 * Created by Clark on 3/30/2017.
 */
public class SessionManagerReadyState extends State {
    public SessionManager getSessionManager() {
        return (SessionManager) getContainer();
    }

    public SessionManagerReadyState(SessionManager sessionManager) {
        super(sessionManager);
    }

    public State start () {
        long period = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_SESSION_GC_PERIOD, MirandaProperties.DEFAULT_SESSION_GC_PERIOD);

        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(null, this);
        Miranda.timer.sendSchedulePeriodic(period, getSessionManager().getQueue(), garbageCollectionMessage);

        return getSessionManager().getCurrentState();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = getSessionManager().getCurrentState();

        switch (message.getSubject()) {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case AddSession: {
                AddSessionMessage addSessionMessage = (AddSessionMessage) message;
                nextState = processAddSessionMessage(addSessionMessage);
                break;
            }

            case SessionsExpired: {
                SessionsExpiredMessage sessionsExpiredMessage = (SessionsExpiredMessage) message;
                nextState = processSessionsExpiredMessage(sessionsExpiredMessage);
                break;
            }

            case CreateSession: {
                CreateSessionMessage createSessionMessage = (CreateSessionMessage) message;
                nextState = processCreateSessionMessage(createSessionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        getSessionManager().performGarbageCollection();

        return getSessionManager().getCurrentState();
    }

    public State processAddSessionMessage(AddSessionMessage addSessionMessage) {
        getSessionManager().createSession(addSessionMessage.getSession().getUser());

        return getSessionManager().getCurrentState();
    }

    public State processSessionsExpiredMessage (SessionsExpiredMessage sessionsExpiredMessage) {
        getSessionManager().expireSessions (sessionsExpiredMessage.getExpiredSessions());

        return getSessionManager().getCurrentState();
    }

    public State processCreateSessionMessage (CreateSessionMessage createSessionMessage) {
        Session session = getSessionManager().createSession(createSessionMessage.getUser());
        CreateSessionResponseMessage response = new CreateSessionResponseMessage(getSessionManager().getQueue(), this, session.getId());
        createSessionMessage.reply(response);

        return getSessionManager().getCurrentState();
    }
}
