package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletHolder;
import com.ltsllc.miranda.session.Session;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 6/11/2017.
 */
public class NewEventHolder extends ServletHolder {
    public static class NewEvent {
        public Event event;
        public Results result;
        public String guid;
    }

    public static final String NAME = "new event holder";

    private static Logger logger = Logger.getLogger(NewEventHolder.class);
    private static NewEventHolder ourInstance;

    private String base;
    private Map<String, Event> eventMap;
    private Map<String, Results> resultMap;

    public NewEventHolder(String name, long timeout, String base) throws MirandaException {
        super(name, timeout);

        this.base = base;
        this.eventMap = new HashMap<String, Event>();
        this.resultMap = new HashMap<String, Results>();
    }

    public String getBase() {
        return base;
    }

    public static NewEventHolder getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(NewEventHolder ourInstance) {
        NewEventHolder.ourInstance = ourInstance;
    }

    public static void initialize(long timeout, String base) throws MirandaException {
        ourInstance = new NewEventHolder(NAME, timeout, base);
    }

    public synchronized void registerGuid(String guid) {
        resultMap.put(guid, Results.Unknown);
    }

    public synchronized void setResultAndAwaken(String guid, Results result, Event event) {
        if (!resultMap.containsKey(guid)) {
            logger.warn("Unrecognized GUID: " + guid);
        }

        resultMap.put(guid, result);
        eventMap.put(guid, event);
    }

    public synchronized void unregisterGuid(String guid) {
        resultMap.remove(guid);
        eventMap.remove(guid);
    }

    public NewEvent create(Session session, Event event) throws TimeoutException {
        try {
            registerGuid(event.getGuid());
            Miranda.getInstance().getEventManager().sendNewEventMessage(getQueue(), this, session, event);

            sleep();

            NewEvent newEvent = new NewEvent();

            newEvent.guid = event.getGuid();
            newEvent.result = getResult(event.getGuid());
            newEvent.event = event;

            return newEvent;
        } finally {
            unregisterGuid(event.getGuid());
        }
    }

    public synchronized Results getResult(String guid) {
        return resultMap.get(guid);
    }

    public synchronized Event getEvent(String guid) {
        return eventMap.get(guid);
    }
}
