package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/7/2017.
 */
public class CreateEventMessage extends Message {
    private Event event;
    private Session session;


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public CreateEventMessage(BlockingQueue<Message> senderQueue, Object sender, Event event, Session session) {
        super(Subjects.CreateEvent, senderQueue, sender);
        setEvent(event);
        setSession(session);
    }
}
