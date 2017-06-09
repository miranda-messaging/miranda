package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.servlet.objects.RequestObject;

/**
 * Created by Clark on 6/8/2017.
 */
public class EventRequestObject extends RequestObject {
    private Event event;

    public EventRequestObject (String sessionId, Event event) {
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
