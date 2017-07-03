package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/7/2017.
 */
public class CreateEventMessage extends Message {
    private Event event;

    public Event getEvent() {
        return event;
    }

    public CreateEventMessage (BlockingQueue<Message> senderQueue, Object sender, Event event) {
        super(Subjects.CreateEvent, senderQueue, sender);

        this.event = event;
    }
}
