package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/11/2017.
 */
public class NewEventMessage extends Message {
    private Session session;
    private Event event;

    public NewEventMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, Event event) {
        super(Subjects.NewEvent, senderQueue, sender);

        this.session = session;
        this.event = event;
    }

    public Session getSession() {
        return session;
    }

    public Event getEvent() {
        return event;
    }
}
