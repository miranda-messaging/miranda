package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Manager;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.messages.*;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class SubscriptionManager extends Manager<Subscription, Subscription> {
    public static final String NAME = "SubscriptionManager";

    public List<Subscription> getSubscriptions() {
        return getData();
    }

    public SubscriptionsFile getSubscriptionsFile() {
        return (SubscriptionsFile) getFile();
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        setData(subscriptions);
    }

    public SubscriptionManager(String filename) {
        super("subscription manager", new SubscriptionsFile(Miranda.getInstance().getWriter(), filename));

        SubscriptionManagerReadyState readyState = new SubscriptionManagerReadyState(this);
        setCurrentState(readyState);
    }

    public void sendOwnerQueryMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        OwnerQueryMessage ownerQueryMessage = new OwnerQueryMessage(senderQueue, sender, name);
        sendToMe(ownerQueryMessage);
    }

    public void sendGetSubscriptionsMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GetSubscriptionsMessage getSubscriptionsMessage = new GetSubscriptionsMessage(senderQueue, sender);
        sendToMe(getSubscriptionsMessage);
    }

    public void sendGetSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetSubscriptionMessage getSubscriptionMessage = new GetSubscriptionMessage(senderQueue, sender, name);
        sendToMe(getSubscriptionMessage);
    }

    public void sendCreateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender,
                                               Session session, Subscription subscription) {

        CreateSubscriptionMessage createSubscriptionMessage = new CreateSubscriptionMessage(senderQueue, sender,
                session, subscription);

        sendToMe(createSubscriptionMessage);
    }

    public void sendUpdateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                               Subscription subscription) {
        UpdateSubscriptionMessage updateSubscriptionMessage = new UpdateSubscriptionMessage(senderQueue, sender,
                session, subscription);

        sendToMe(updateSubscriptionMessage);
    }

    public void sendDeleteSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                               String name) {

        DeleteSubscriptionMessage deleteSubscriptionMessage = new DeleteSubscriptionMessage(senderQueue, sender,
                session, name);

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

    public void sendGarbageCollectionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(senderQueue, sender);
        sendToMe(garbageCollectionMessage);
    }

    public Subscription convert (Subscription subscription) {
        return subscription;
    }
}
