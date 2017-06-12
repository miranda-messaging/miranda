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

package com.ltsllc.miranda.event;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.event.messages.NewEventMessage;

/**
 * Created by Clark on 5/14/2017.
 */
public class EventManagerReadyState extends State {
    public EventManager getEventManager () {
        return (EventManager) getContainer();
    }

    public EventManagerReadyState (EventManager eventManager) {
        super(eventManager);
    }

    public State processMessage (Message message) {
        State nextState = getEventManager().getCurrentState();

        switch (message.getSubject()) {
            case NewEvent: {
                NewEventMessage newEventMessage = (NewEventMessage) message;
                nextState = processEventMessage (newEventMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processEventMessage (NewEventMessage message) {
        NewEventOperation newEventOperation = new NewEventOperation (getEventManager(), message.getSession(),
                message.getGuid(), message.getMethod(), message.getContent());

        newEventOperation.start();

        return getEventManager().getCurrentState();
    }
}
