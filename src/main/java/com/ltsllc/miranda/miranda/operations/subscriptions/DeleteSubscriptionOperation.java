package com.ltsllc.miranda.miranda.operations.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.Subscription;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionOperation extends Operation {
    private String subscriptionName;
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public DeleteSubscriptionOperation (BlockingQueue<Message> requester, Session session, String subscriptionName) {
        super("delete subscription operation", requester, session);

        this.subscriptionName = subscriptionName;

        DeleteSubscriptionOperationReadyState readyState = new DeleteSubscriptionOperationReadyState(this);
        setCurrentState(readyState);
    }

    public void start () {
        super.start();

        Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionMessage(getQueue(), this, getSubscriptionName());
    }
}
