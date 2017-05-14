package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.manager.ManagerStartState;

/**
 * Created by Clark on 5/14/2017.
 */
public class SubscriptionManagerStartState extends ManagerStartState {
    public SubscriptionManager getSubscriptionManager () {
        return (SubscriptionManager) getContainer();
    }

    public SubscriptionManagerStartState (SubscriptionManager subscriptionManager) {
        super(subscriptionManager);
    }

    public State getReadyState () {
        return new SubscriptionManagerReadyState(getSubscriptionManager());
    }
}
