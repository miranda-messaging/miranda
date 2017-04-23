package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.List;

/**
 * Created by Clark on 4/22/2017.
 */
public class SubscriptionsResult extends ResultObject {
    private List<Subscription> subscriptions;

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
