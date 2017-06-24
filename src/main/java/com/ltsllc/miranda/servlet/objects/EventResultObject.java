package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.servlet.objects.ResultObject;

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
