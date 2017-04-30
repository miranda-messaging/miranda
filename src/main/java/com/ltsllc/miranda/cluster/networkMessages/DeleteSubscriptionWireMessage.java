package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.subsciptions.Subscription;

/**
 * Created by Clark on 4/30/2017.
 */
public class DeleteSubscriptionWireMessage extends WireMessage {
    private String name;

    public String getName() {
        return name;
    }

    public DeleteSubscriptionWireMessage(String name) {
        super(WireSubjects.DeleteSubscription);

        this.name = name;
    }
}
