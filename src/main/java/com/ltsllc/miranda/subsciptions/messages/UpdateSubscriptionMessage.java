package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class UpdateSubscriptionMessage extends Message {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public UpdateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Subscription subscription) {
        super(Subjects.UpdateSubscription, senderQueue, sender);

        this.subscription = subscription;
    }
}
