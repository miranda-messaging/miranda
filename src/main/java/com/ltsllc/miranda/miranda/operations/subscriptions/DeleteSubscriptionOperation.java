package com.ltsllc.miranda.miranda.operations.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionOperation extends Operation {
    private String subscriptionName;

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
        Miranda.getInstance().getSubscriptionManager().sendDeleteSubscriptionMessage(getQueue(), this, getSession(),
                getSubscriptionName());
    }
}
