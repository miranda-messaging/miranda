package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Subscription;
import com.ltsllc.miranda.server.NewObjectHandlerReadyState;
import com.ltsllc.miranda.subsciptions.NewSubscriptionHandler;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;

import java.lang.reflect.Type;

/**
 * Created by Clark on 2/18/2017.
 */
public class NewSubscriptionHandlerReadyState extends NewObjectHandlerReadyState<SubscriptionsFile, Subscription, NewSubscriptionHandler> {
    public Type getBasicType() {
        return Subscription.class;
    }

    public NewSubscriptionHandlerReadyState(Consumer consumer, SubscriptionsFile subscriptionsFile, NewSubscriptionHandler handler) {
        super(consumer, subscriptionsFile, handler);
    }
}
