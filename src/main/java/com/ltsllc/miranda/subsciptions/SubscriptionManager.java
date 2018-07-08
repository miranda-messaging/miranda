/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.manager.StandardManager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.messages.*;
import com.ltsllc.miranda.subsciptions.states.SubscriptionManagerReadyState;
import com.ltsllc.miranda.subsciptions.states.SubscriptionManagerStartState;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class SubscriptionManager extends StandardManager<Subscription> {
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

    public SubscriptionManager(String filename) throws IOException, MirandaException {
        super("subscription manager", filename);

        SubscriptionManagerStartState subscriptionManagerStartState = new SubscriptionManagerStartState(this);
        setCurrentState(subscriptionManagerStartState);
    }

    public SingleFile createFile(String filename) throws IOException, MirandaException {
        return new SubscriptionsFile(Miranda.getInstance().getReader(), Miranda.getInstance().getWriter(), filename);
    }

    public State createStartState() throws MirandaException {
        return new SubscriptionManagerStartState(this);
    }

    public void sendOwnerQueryMessage(BlockingQueue<Message> senderQueue, Object sender, String name) {
        OwnerQueryMessage ownerQueryMessage = new OwnerQueryMessage(senderQueue, sender, name);
        sendToMe(ownerQueryMessage);
    }

    public void sendGetSubscriptionsMessage(BlockingQueue<Message> senderQueue, Object sender) {
        LIstSubscriptionsMessage getSubscriptionsMessage = new LIstSubscriptionsMessage(senderQueue, sender);
        sendToMe(getSubscriptionsMessage);
    }

    public void sendGetSubscriptionMessage(BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetSubscriptionMessage getSubscriptionMessage = new GetSubscriptionMessage(senderQueue, sender, name);
        sendToMe(getSubscriptionMessage);
    }

    public void sendCreateSubscriptionMessage(BlockingQueue<Message> senderQueue, Object sender,
                                              Session session, Subscription subscription) {

        CreateSubscriptionMessage createSubscriptionMessage = new CreateSubscriptionMessage(senderQueue, sender,
                session, subscription);

        sendToMe(createSubscriptionMessage);
    }

    public void sendUpdateSubscriptionMessage(BlockingQueue<Message> senderQueue, Object sender, Session session,
                                              Subscription subscription) {
        UpdateSubscriptionMessage updateSubscriptionMessage = new UpdateSubscriptionMessage(senderQueue, sender,
                session, subscription);

        sendToMe(updateSubscriptionMessage);
    }

    public void sendDeleteSubscriptionMessage(BlockingQueue<Message> senderQueue, Object sender, Session session,
                                              String name) {

        DeleteSubscriptionMessage deleteSubscriptionMessage = new DeleteSubscriptionMessage(senderQueue, sender,
                session, name);

        sendToMe(deleteSubscriptionMessage);
    }

    public Results createSubscription(Subscription subscription) {
        Subscription existing = findSubscription(subscription.getName());
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

    public Results updateSubscription(Subscription subscription) {
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

    public Results deleteSubscription(String name) {
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

    public Subscription convert(Subscription subscription) {
        return subscription;
    }

    public State getReadyState () throws MirandaException {
        return new SubscriptionManagerReadyState(this);
    }

    public void sendNewEvent(Event event, BlockingQueue<Message> senderQueue, Object sender) {
        NewEventMessage newEventMessage = new NewEventMessage(senderQueue, sender, null, event);
        sendToMe(newEventMessage);
    }
}
