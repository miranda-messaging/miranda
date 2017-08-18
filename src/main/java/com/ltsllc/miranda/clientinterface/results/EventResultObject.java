package com.ltsllc.miranda.clientinterface.results;


import com.ltsllc.miranda.clientinterface.basicclasses.Event;

/**
 * Created by clarkhobbie on 6/22/17.
 */
public class EventResultObject extends ResultObject {
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
