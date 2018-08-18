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

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.basicclasses.Mergeable;
import com.ltsllc.miranda.event.messages.*;
import com.ltsllc.miranda.event.states.EventManagerReadyState;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.page.EventRecord;
import com.ltsllc.miranda.page.PageCache;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.panics.StartupPanic;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.file.messages.ListMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * A DirectoryManager that handles Events
 */
public class EventManager extends DirectoryManager {
    public static final String NAME = "event manager";


    private PageCache pageCache;
    private Map<String, EventRecord> eventMap = new HashMap<>();

    public EventManager(String directoryName, int objectLimit, Reader reader, Writer writer) throws IOException, MirandaException {
        super(NAME, directoryName, objectLimit, reader, writer);

        EventManagerReadyState eventManagerReadyState = new EventManagerReadyState(this);
        setCurrentState(eventManagerReadyState);
        pageCache = new PageCache(directoryName,5, objectLimit, Miranda.getInstance().getWriter());
        pageCache.start();
    }

    public PageCache getPageCache() {
        return pageCache;
    }

    public Map<String, EventRecord> getEventMap() {
        return eventMap;
    }

    public void setEventMap(Map<String, EventRecord> eventMap) {
        this.eventMap = eventMap;
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
            EvictMessage evictEventsMessage = new EvictMessage(getQueue(), this);
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

    public boolean createEvent(Event event) {
        if (eventMap.containsKey(event.getGuid()))
            return false;

        eventMap.put(event.getGuid(), new EventRecord());
        getPageCache().addEvent(event);
        return true;
    }

    public State getReadyState () {
        try {
            return new EventManagerReadyState(this);
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception creating ready state", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return null;
        }
    }

    @Override
    public void processEntry(String string) {
        try {
            EventsFile eventsFile = new EventsFile(string, Miranda.getInstance().getReader(),
                    Miranda.getInstance().getWriter());
        } catch (Exception e) {
            Panic panic = new Panic("Exception processing an entry", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
        }
    }

    public void sendGetEvent(String eventId, BlockingQueue<Message> senderQueue, Object senderObject) {
        GetEventMessage getEventMessage = new GetEventMessage(eventId, senderQueue, senderObject);
        sendToMe(getEventMessage);
    }

    public Event lookupEvent (String eventId) {
        EventRecord eventRecord = getEventMap().get(eventId);

        if (null == eventRecord) {
            return null;
        }

        if (eventRecord.isOnline()) {
            Event event = getPageCache().getEvent(eventId);
            return event;
        } else {
            Event event = findEvent(eventId);
            return event;
        }
    }

    public 
}
