package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class GetSubscriptionsResponseMessage extends Message {
    private List<Subscription> subscriptions;

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public GetSubscriptionsResponseMessage(BlockingQueue<Message> senderQueue, Object sender, List<Subscription> subscriptions) {
        super(Subjects.GetSubscriptionsResponse, senderQueue, sender);

        this.subscriptions = subscriptions;
    }
}
