package com.ltsllc.miranda.clientinterface.requests;

import com.ltsllc.miranda.clientinterface.basicclasses.Event;

/**
 * Created by Clark on 6/7/2017.
 */
public class EventRequest extends Request {
    private Event event;

    public EventRequest(String sessionId, Event event) {
        super(sessionId);

        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
