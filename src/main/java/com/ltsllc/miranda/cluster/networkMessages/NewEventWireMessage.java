package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * Created by Clark on 6/11/2017.
 */
public class NewEventWireMessage extends WireMessage {
    private Event event;
    private String key;

    public NewEventWireMessage (String key, Event event) {
        super(WireSubjects.NewEvent);

        this.key = key;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public String getKey() {
        return key;
    }
}
