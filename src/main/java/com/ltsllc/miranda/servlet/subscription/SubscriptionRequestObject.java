package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.servlet.objects.RequestObject;
import com.ltsllc.miranda.subsciptions.Subscription;

/**
 * Created by Clark on 4/28/2017.
 */
public class SubscriptionRequestObject extends RequestObject {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
