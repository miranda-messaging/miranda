package com.ltsllc.miranda.clientinterface.basicclasses;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

public class EventQueue implements Cloneable, Mergeable {
    private long lastChange;
    private List<Event> events = new LinkedList();
    private String filename;

    @Override
    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filenme) {
        this.filename = filenme;
    }

    public List getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public EventQueue (String filename)
    {
        setFilename(filename);
    }

    public void newEvent (Event event) {
        getEvents().add(event);
        String json = event.toJson();

    }

    public void copyFrom (Mergeable mergeable) {
        EventQueue other = (EventQueue) mergeable;

        List<Event> newEvents = new LinkedList<>(other.events);
    }

    public boolean isEquivalentTo (Mergeable object) {
        EventQueue other = (EventQueue) object;
        return MirandaObject.listsAreEqual(getEvents(), other.getEvents());
    }

    public String toJson () {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }

    @Override
    public boolean merge(Mergeable mergeable) {
        EventQueue other = (EventQueue) mergeable;
        if (getLastChange() > other.getLastChange())
            return false;
        else {
            copyFrom(other);
            return true;
        }
    }

    public Object clone () throws CloneNotSupportedException {
        return super.clone();
    }
}
