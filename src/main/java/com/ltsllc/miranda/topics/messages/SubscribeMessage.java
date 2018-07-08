package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class SubscribeMessage extends Message {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public SubscribeMessage (Subscription subscription, BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.Subscribe, senderQueue, senderObject);
        setSubscription(subscription);
    }
}
