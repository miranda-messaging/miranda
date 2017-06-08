package com.ltsllc.miranda.servlet.event;

import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.servlet.objects.RequestObject;

/**
 * Created by Clark on 6/7/2017.
 */
public class EventRequest extends RequestObject {
    private Event event;

    public EventRequest (String sessionId, Event event){
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
