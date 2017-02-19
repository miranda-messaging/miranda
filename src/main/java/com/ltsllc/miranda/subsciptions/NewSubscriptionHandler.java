package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.server.NewObjectHandlerReadyState;
import com.ltsllc.miranda.server.NewObjectPostHandler;

/**
 * Created by Clark on 2/18/2017.
 */
public class NewSubscriptionHandler extends NewObjectPostHandler<SubscriptionsFile> {
    public NewSubscriptionHandler (SubscriptionsFile subscriptionsFile) {
        super(subscriptionsFile);

        NewObjectHandlerReadyState readyState = new NewSubscriptionHandlerReadyState(this, getFile(), this);
        setCurrentState(readyState);
    }
}
