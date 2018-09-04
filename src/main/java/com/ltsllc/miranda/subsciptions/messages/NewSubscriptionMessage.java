package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class NewSubscriptionMessage extends Message {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public NewSubscriptionMessage(BlockingQueue<Message> queue, Object senderObject, Subscription subscription) {
        super(Subjects.NewSubscription, queue, senderObject);
        setSubscription(subscription);
    }
}
