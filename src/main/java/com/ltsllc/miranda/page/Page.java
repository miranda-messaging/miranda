package com.ltsllc.miranda.page;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;

import java.util.*;

/**
 * A collection of {@link com.ltsllc.miranda.clientinterface.basicclasses.Event}
 */
public class Page {
    private transient boolean isDirty = false;
    private transient boolean isBeingWritten = false;
    private Map<String, Event> eventMap = new HashMap<>();
    private transient Gson gson;

    public boolean isBeingWritten() {
        return isBeingWritten;
    }

    public void setBeingWritten(boolean beingWritten) {
        isBeingWritten = beingWritten;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public Map<String, Event> getEventMap() {
        return eventMap;
    }

    public void setEventMap(Map<String, Event> eventMap) {
        this.eventMap = eventMap;
    }

    public int getNumberOfEvents() {
        return eventMap.size();
    }

    public Page () {
        intialize();
    }

    public void empty() {
        setEventMap(new HashMap<String, Event>());
        setDirty(false);
    }

    public void addEvent (Event event) {
        getEventMap().put (event.getGuid(), event);
        setDirty(true);
    }

    public Event getEvent (String guid) {
        return getEventMap().get(guid);
    }

    public void intialize () {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        setGson(gsonBuilder.create());
    }

    public String toJson() {
        Collection<Event> events = getEventMap().values();
        Enumeration<Event> enumeration = Collections.enumeration(events);
        List<Event> eventList = Collections.list(enumeration);
        String json = getGson().toJson(eventList);
        return json;
    }
}
