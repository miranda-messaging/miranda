package com.ltsllc.miranda.deliveries.messages;

import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class DeliverEventMessage extends Message {
    private Event event;
    private Subscription subscription;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public DeliverEventMessage (Event event, Subscription subscription, BlockingQueue<Message> senderQueue,
                                Object senderObject) {
        super (Subjects.DeliverEvent, senderQueue, senderObject);
        setEvent(event);
        setSubscription(subscription);
    }
}
