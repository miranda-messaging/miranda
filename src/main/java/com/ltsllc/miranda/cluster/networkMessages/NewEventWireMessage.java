package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.clientinterface.basicclasses.Event;

/**
 * Created by Clark on 6/11/2017.
 */
public class NewEventWireMessage extends ConversationWireMessage {
    private Event event;

    public NewEventWireMessage(String key, Event event) {
        super(WireSubjects.NewEvent, key);

        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
