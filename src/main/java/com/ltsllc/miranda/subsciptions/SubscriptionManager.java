package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.messages.*;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class SubscriptionManager extends Consumer {
    public static final String NAME = "SubscriptionManager";

    private SubscriptionsFile subscriptionsFile;
    private List<Subscription> subscriptions;

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public SubscriptionsFile getSubscriptionsFile() {

        return subscriptionsFile;
    }

    public SubscriptionManager(String filename) {
        super("subscription manager");

        Miranda miranda = Miranda.getInstance();
        subscriptionsFile = new SubscriptionsFile(miranda.getWriter(), filename);

        SubscriptionManagerReadyState readyState = new SubscriptionManagerReadyState(this);
        setCurrentState(readyState);
    }

    public void sendOwnerQueryMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        OwnerQueryMessage ownerQueryMessage = new OwnerQueryMessage(senderQueue, sender, name);
        sendToMe(ownerQueryMessage);
    }

    public void performGarbageCollection () {
    }

    public void sendGetSubscriptionsMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GetSubscriptionsMessage getSubscriptionsMessage = new GetSubscriptionsMessage(senderQueue, sender);
        sendToMe(getSubscriptionsMessage);
    }

    public void sendGetSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetSubscriptionMessage getSubscriptionMessage = new GetSubscriptionMessage(senderQueue, sender, name);
        sendToMe(getSubscriptionMessage);
    }

    public void sendCreateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Subscription subscription) {
        CreateSubscriptionMessage createSubscriptionMessage = new CreateSubscriptionMessage(senderQueue, sender, subscription);
        sendToMe(createSubscriptionMessage);
    }

    public void sendUpdateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Subscription subscription) {
        UpdateSubscriptionMessage updateSubscriptionMessage = new UpdateSubscriptionMessage(senderQueue, sender, subscription);
        sendToMe(updateSubscriptionMessage);
    }

    public void sendDeleteSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        DeleteSubscriptionMessage deleteSubscriptionMessage = new DeleteSubscriptionMessage(senderQueue, sender, name);
        sendToMe(deleteSubscriptionMessage);
    }

    public Results createSubscription (Subscription subscription) {
        Subscription existing = findSubscription (subscription.getName());
        if (null != existing) {
            return Results.Duplicate;
        }

        getSubscriptions().add(subscription);
        getSubscriptionsFile().sendAddObjectsMessage(getQueue(), this, subscription);

        return Results.Success;
    }

    public Subscription findSubscription(String name) {
        for (Subscription subscription : getSubscriptions()) {
            if (name.equals(subscription.getName()))
                return subscription;
        }

        return null;
    }

    public Results updateSubscription (Subscription subscription) {
        Subscription existing = findSubscription(subscription.getName());
        Results result;

        if (null == existing) {
            result = Results.SubscriptionNotFound;
        } else {
            existing.updateFrom(subscription);
            result = Results.Success;
        }

        return result;
    }

    public Results deleteSubscription (String name) {
        Subscription subscription = findSubscription(name);

        Results result;

        if (null == subscription) {
            result = Results.SubscriptionNotFound;
        } else {
            getSubscriptions().remove(subscription);
            result = Results.Success;
        }

        return result;
    }
}
