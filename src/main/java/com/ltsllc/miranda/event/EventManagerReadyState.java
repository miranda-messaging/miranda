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
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.events.NewEventOperation;

/**
 * Created by Clark on 5/14/2017.
 */
public class EventManagerReadyState extends State {
    public EventManager getEventManager () {
        return (EventManager) getContainer();
    }

    public EventManagerReadyState (EventManager eventManager) throws MirandaException {
        super(eventManager);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getEventManager().getCurrentState();

        switch (message.getSubject()) {
            case NewEvent: {
                NewEventMessage newEventMessage = (NewEventMessage) message;
                nextState = processNewEventMessage (newEventMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processNewEventMessage (NewEventMessage message) throws MirandaException {
        NewEventOperation newEventOperation = new NewEventOperation (getEventManager(),
                Miranda.getInstance().getTopicManager(), Miranda.getInstance().getCluster(), message.getSession(),
                message.getSender(), message.getEvent());

        newEventOperation.start();

        return getEventManager().getCurrentState();
    }
}
