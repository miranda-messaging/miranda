package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.subsciptions.Subscription;

/**
 * Created by Clark on 4/30/2017.
 */
public class UpdateSubscriptionWireMessage extends WireMessage {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public UpdateSubscriptionWireMessage(Subscription subscription) {
        super(WireSubjects.UpdateSubscription);

        this.subscription = subscription;
    }
}
