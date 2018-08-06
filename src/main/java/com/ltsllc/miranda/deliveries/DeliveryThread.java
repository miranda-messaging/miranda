package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread that delivers events to subscriptions.
 */
public class DeliveryThread extends Consumer {
    private Subscription subscription;
    private Event event;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public DeliveryThread (Subscription subscription, Event event) {
        super("DeliveryThread", new LinkedBlockingQueue<Message>());

        setSubscription(subscription);
        setEvent(event);
    }
}
