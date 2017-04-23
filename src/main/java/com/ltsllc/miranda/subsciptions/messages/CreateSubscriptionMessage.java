package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class CreateSubscriptionMessage extends Message {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public CreateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Subscription subscription) {
        super (Subjects.CreateSubscription, senderQueue, sender);

        this.subscription = subscription;
    }
}
