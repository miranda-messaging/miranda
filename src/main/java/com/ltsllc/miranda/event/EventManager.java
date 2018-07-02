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

import com.ltsllc.miranda.event.states.EventManagerReadyState;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.StartupPanic;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.event.messages.CreateEventMessage;
import com.ltsllc.miranda.event.messages.EvictMessage;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.event.messages.ReadEventMessage;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.file.messages.ListMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * A DirectoryManager that handles Events
 */
public class EventManager extends DirectoryManager {
    public static final String NAME = "event manager";

    private Map<String, Event> eventMap = new HashMap<>();

    public EventManager(String directoryName, int objectLimit, Reader reader, Writer writer) throws IOException, MirandaException {
        super(NAME, directoryName, objectLimit, reader, writer);

        EventManagerReadyState eventManagerReadyState = new EventManagerReadyState(this);
        setCurrentState(eventManagerReadyState);
    }

    /**
     * Start the manager.
     * <p>
     * This entails starting periodic evictions.
     * </p>
     */
    public void start() {
        try {
            super.start();

            long period = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_EVENT_EVICTION_PERIOD);
            EvictMessage evictEventsMessage = new EvictMessage();
            Miranda.timer.sendSchedulePeriodic(0, period, getQueue(), evictEventsMessage);
        } catch (MirandaException e) {
            StartupPanic startupPanic = new StartupPanic("Exception starting event manager", e,
                    StartupPanic.StartupReasons.ExceptionStartingEventManager);

            Miranda.panicMiranda(startupPanic);
        }
    }

    public void sendReadEventMessage(BlockingQueue<Message> senderQueue, Object sender, String guid) {
        ReadEventMessage readEventMessage = new ReadEventMessage(senderQueue, sender, guid);
        sendToMe(readEventMessage);
    }

    public void sendCreateEventMessage(BlockingQueue<Message> senderQueue, Object sender, Event event, Session session) {
        CreateEventMessage createEventMessage = new CreateEventMessage(senderQueue, sender, event, session);
        sendToMe(createEventMessage);
    }

    public void sendListMessage(BlockingQueue<Message> senderQueue, Object sender) {
        ListMessage listMessage = new ListMessage(senderQueue, sender);
        sendToMe(listMessage);
    }

    public void sendNewEventMessage(BlockingQueue<Message> senderQueue, Object sender, Session session, Event event) {
        NewEventMessage newEventMessage = new NewEventMessage(senderQueue, sender, session, event);
        sendToMe(newEventMessage);
    }

    public void createEvent(Event event) {
        eventMap.put(event.getGuid(), event);
    }
}
