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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.session.messages.*;
import com.ltsllc.miranda.session.operations.CreateSessionOperation;


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

            case GetSession: {
                GetSessionMessage getSessionMessage = (GetSessionMessage) message;
                nextState = processGetSessionMessage (getSessionMessage);
                break;
            }

            case CheckSession: {
                CheckSessionMessage checkSessionMessage = (CheckSessionMessage) message;
                nextState = processCheckSessionMessage(checkSessionMessage);
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
        CreateSessionResponseMessage response = new CreateSessionResponseMessage(getSessionManager().getQueue(),
                this, Results.Success, session);

        createSessionMessage.reply(response);

        return getSessionManager().getCurrentState();
    }

    public State processGetSessionMessage (GetSessionMessage getSessionMessage) {
        Session session = getSessionManager().getSessionFor(getSessionMessage.getName());

        if (null == session) {
            CreateSessionOperation createSessionOperation = new CreateSessionOperation(getSessionMessage.getSender(),
                    null, getSessionMessage.getName());

            createSessionOperation.start();
        } else {
            GetSessionResponseMessage response = new GetSessionResponseMessage(getSessionManager().getQueue(),
                    this, Results.Success, session);

            getSessionMessage.reply(response);
        }

        return getSessionManager().getCurrentState();
    }

    public State processCheckSessionMessage (CheckSessionMessage checkSessionMessage) {
        Session session = getSessionManager().checkSession(checkSessionMessage.getSessionId());

        Results result;
        if (session == null)
            result = Results.SessionNotFound;
        else
            result = Results.Success;

        CheckSessionResponseMessage response = new CheckSessionResponseMessage(getSessionManager().getQueue(),
                this, result, session);

        checkSessionMessage.reply(response);

        return getSessionManager().getCurrentState();
    }

}
