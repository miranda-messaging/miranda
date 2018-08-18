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

package com.ltsllc.miranda.event.states;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.event.EventManager;
import com.ltsllc.miranda.event.messages.*;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.events.CreateEventOperation;
import com.ltsllc.miranda.operations.events.NewEventOperation;

/**
 * When the {@link EventManager} is in this state, it is ready to handle new events
 */
public class EventManagerReadyState extends State {
    public EventManager getEventManager() {
        return (EventManager) getContainer();
    }

    public EventManagerReadyState(EventManager eventManager) throws MirandaException {
        super(eventManager);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getEventManager().getCurrentState();

        switch (message.getSubject()) {
            case CreateEvent: {
                CreateEventMessage createEventMessage = (CreateEventMessage) message;
                nextState = processCreateEventMessage(createEventMessage);
                break;
            }

            case Evict: {
                EvictMessage evictMessage = (EvictMessage) message;
                nextState = processEvictMessage (evictMessage);
                break;
            }

            case GetEvent: {
                GetEventMessage getEventMessage = (GetEventMessage) message;
                nextState = processGetEventMessage(getEventMessage);
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateEventMessage(CreateEventMessage createEventMessage) throws MirandaException {
        Event event = createEventMessage.getEvent();
        Results result;

        if (getEventManager().createEvent(event))
            result = Results.Success;
        else
            result = Results.Duplicate;

        CreateEventResponseMessage createEventResponseMessage = new CreateEventResponseMessage(getEventManager().getQueue(),
                getEventManager(), result, createEventMessage.getEvent().getGuid());

        createEventMessage.reply(createEventResponseMessage);

        tellSubsciptionManager(event);
        tellCluster(event);

        return getEventManager().getCurrentState();
    }

    public State processEvictMessage(EvictMessage evictMessage) {
        EvictMessage evictMessage2 = new EvictMessage(getEventManager().getQueue(), getEventManager());
        send(getEventManager().getPageCache().getQueue(), evictMessage2);

        return getEventManager().getCurrentState();
    }

    public void tellSubsciptionManager (Event event) {
        Miranda.getInstance().getSubscriptionManager().sendNewEvent(event, getEventManager().getQueue(),
                getEventManager());
    }

    public void tellCluster (Event event) {
        Miranda.getInstance().getCluster().sendNewEvent(event, getEventManager().getQueue(), getEventManager());
    }

    public State processGetEventMessage (GetEventMessage getEventMessage) {
        Results result = null;
        Event event = getEventManager().getEvent(getEventMessage.getEventId());
        if (event != null){
            result = Results.Success;
        } else {
            result = Results.Failure;
        }

        GetEventReplyMessage getEventReplyMessage = new GetEventReplyMessage(result, event, getEventManager().getQueue(),
                getEventManager());
    }
}
