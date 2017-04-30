package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.subsciptions.Subscription;

/**
 * Created by Clark on 4/30/2017.
 */
public class NewSubscriptionWireMessage extends WireMessage {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public NewSubscriptionWireMessage (Subscription subscription) {
        super(WireSubjects.NewSubscription);

        this.subscription = subscription;
    }
}
