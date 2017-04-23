package com.ltsllc.miranda.miranda.operations.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.session.operations.CreateSessionOperation;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class CreateSubscriptionOperation extends Operation {
    private Subscription subscription;
    private boolean userManagerResponded;
    private boolean topicManagerResponded;

    public boolean getTopicManagerResponded() {
        return topicManagerResponded;
    }

    public void setTopicManagerResponded(boolean topicManagerResponded) {
        this.topicManagerResponded = topicManagerResponded;
    }

    public boolean getUserManagerResponded() {
        return userManagerResponded;
    }

    public void setUserManagerResponded(boolean userManagerResponded) {
        this.userManagerResponded = userManagerResponded;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public CreateSubscriptionOperation (BlockingQueue<Message> requester, Subscription subscription) {
        super("create subscription operation", requester);

        CreateSubscriptionOperationReadyState readyState = new CreateSubscriptionOperationReadyState(this);
        setCurrentState(readyState);
    }

    public void start () {
        Miranda miranda = Miranda.getInstance();
        miranda.getUserManager().sendGetUser(getQueue(), this, getSubscription().getOwner());
        miranda.getTopicManager().sendGetTopicMessage(getQueue(), this, getSubscription().getTopic());
    }

}
