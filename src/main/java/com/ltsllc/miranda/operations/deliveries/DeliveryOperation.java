package com.ltsllc.miranda.operations.deliveries;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.EventQueue;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.LinkedBlockingQueue;

public class DeliveryOperation extends Operation {
    private Event event;
    private EventQueue eventQueue;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    public DeliveryOperation (Event event, EventQueue eventQueue, Session session) throws MirandaException {
        super("Event Delivery", new LinkedBlockingQueue<Message>(), session);
        setEvent(event);
        setEventQueue(eventQueue);

        setCurrentState(new DeliveryOperationReadyState(this));
    }

}
