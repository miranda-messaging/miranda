package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.subsciptions.Subscription;

/**
 * Created by Clark on 4/22/2017.
 */
public class SubscriptionResultObject extends ResultObject {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
