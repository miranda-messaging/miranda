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

package com.ltsllc.miranda.clientinterface.basicclasses;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.MirandaUncheckedException;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.subsciptions.states.SubscriptionReadyState;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A subscription to a topic.
 * <p>
 * <h3>Attributes</h3>
 * <table border="1">
 *
 * <td><b>Name</b></td>
 * <td><b>Type</b></td>
 * <td><b>Description</b></td>
 *
 * <tr>
 * <td>name</td>
 * <td>String</td>
 * <td>The name of the subscription.  This must be unique among all Subscriptions</td>
 * </tr>
 * <tr>
 * <td>owner</td>
 * <td>String</td>
 * <td>The User that owns the Subscription.  This must refer to the name of an existing User.</td>
 * </tr>
 * <tr>
 * <td>topic</td>
 * <td>String</td>
 * <td>The name of the Topic that the subscription is for.
 * A topic name must refer to an existing Topic.</td>
 * </tr>
 * <tr>
 * <td>dataUrl</td>
 * <td>String</td>
 * <td>The URL that the system will attempt to deliver Events to.</td>
 * </tr>
 * <tr>
 * <td>livelinessUrl</td>
 * <td>String</td>
 * <td>The URL that the system will use to determine if the Subscription is online.
 * If the system thinks that the Subscription is offline, it will do an
 * HTTP GET against this URL to determine if it is online.  A successful result
 * (a 200 code) indicates that the Subscription is online. Any other result and
 * the system will conclude that the Subascription is offline.</td>
 * </tr>
 * <tr>
 * <td>errorPolicy</td>
 * <td>enum</td>
 * <td>How the subscription handles error (non 200 code) results.
 * The allowable values are Drop, Retry and DeadLetter.
 * <p>
 * <p>
 * When the system has an Event that it wants to deliver to a Subscription,
 * it tries to perform an HTTP operation against the Subscription's
 * dataUrl.  If the operation succeeds (the HTTP result is a 200 code),
 * then the system creates a new Delivery and removes the Event from the
 * Subscription's queue.  If the operation fails (a non 200 code),
 * the system consults the Suscription's errorPolicy.
 * </p>
 * <p>
 * <p>
 * When a Subscription has an error policy of Drop,
 * a failure to deliver an Event results in that Event being discarded from the
 * Subscription's queue.
 * </p>
 * <p>
 * <p>
 * When a Subscription has an error policy of Retry,
 * a failure to deliver an Event results in the system trying to deliver the
 * Event again.  Each time the system fails, it waits twice as long before attempting
 * a redelivery.  While retrying the Event, other Events will backup behind the retrying Event.
 * If a Subscription's queue gets longer than a configurable threshold,
 * new Events will be discarded in favor of old events.
 * </p>
 * <p>
 * <p>
 * After a configurable number of tries, the system will discard the Event.
 * </p>
 * <p>
 * <p>
 * When a Subscription has an error policy of DeadLetter,
 * it will put a failing Event into a "dead letter queue".
 * The max size of a dead letter queue is configurable.
 * When another Event needs to be added to a full dead letter queue,
 * it is discarded instead.
 * </p>
 * </td>
 * </tr>
 * </table>
 */
public class Subscription extends Consumer implements Mergeable,Equivalent {
    public enum ErrorPolicies {
        Drop,
        Retry,
        DeadLetter
    }

    private String owner;
    private String topic;
    private String dataUrl;
    private String livelinessUrl;
    private ErrorPolicies errorPolicy;
    private transient EventQueue eventQueue;
    private transient DeadLetterQueue deadLetterQueue;
    private boolean isLocal;


    public void setEventQueue(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }


    public DeadLetterQueue getDeadLetterQueue() {
        return deadLetterQueue;
    }

    public EventQueue getEventQueue () {
        return eventQueue;
    }

    public void setDeadLetterQueue(DeadLetterQueue deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
    }


    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof Subscription))
            return false;

        Subscription other = (Subscription) o;
        if (!(MirandaObject.stringsAreEqual(owner, other.owner)))
            return false;

        if (!(MirandaObject.stringsAreEqual(dataUrl, other.dataUrl)))
            return false;

        return MirandaObject.stringsAreEqual(livelinessUrl, other.livelinessUrl);
    }

    public void copyFrom(Object o) {
        try {
            Subscription other = (Subscription) o;

            setName(other.getName());
            owner = other.owner;
            topic = other.topic;
            dataUrl = other.dataUrl;
            livelinessUrl = other.livelinessUrl;
            errorPolicy = other.errorPolicy;
            eventQueue = (EventQueue) other.clone();
            deadLetterQueue.copyFrom(other.deadLetterQueue);
            isLocal = other.isLocal;
        } catch (CloneNotSupportedException e) {
            throw new MirandaUncheckedException("exception while copying", e);
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public Subscription() {
        super();
        setQueue(new LinkedBlockingQueue<Message>());

        EventQueue eventQueue = new EventQueue(this);
        eventQueue.start();
        setEventQueue(eventQueue);

        setCurrentState(new SubscriptionReadyState(this));
    }


    public Subscription(String name, String owner, String topic, String dataUrl, String livelinessUrl,
                        ErrorPolicies errorPolicy, String queueDirectory) {
        super(name, new LinkedBlockingQueue<Message>());

        setName(name);
        this.owner = owner;
        this.topic = topic;
        this.dataUrl = dataUrl;
        this.livelinessUrl = livelinessUrl;
        this.errorPolicy = errorPolicy;

        String filename = queueDirectory + File.separator + name + ".json";

        this.eventQueue = new EventQueue(this);
        getEventQueue().start();

        this.deadLetterQueue = new DeadLetterQueue();
    }

    public ErrorPolicies getErrorPolicy() {
        return errorPolicy;
    }

    public void setErrorPolicy(ErrorPolicies errorPolicy) {
        this.errorPolicy = errorPolicy;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLivelinessUrl() {
        return livelinessUrl;
    }

    public void setLivelinessUrl(String livelinessUrl) {
        this.livelinessUrl = livelinessUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public void updateFrom(Subscription other) {
        setOwner(other.getOwner());
        setDataUrl(other.getDataUrl());
        setLivelinessUrl(other.getLivelinessUrl());
        setErrorPolicy(other.getErrorPolicy());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!super.equals(o))
            return false;

        Subscription other = (Subscription) o;

        if (!MirandaObject.stringsAreEqual(getDataUrl(), other.getDataUrl()))
            return false;

        if (!MirandaObject.stringsAreEqual(getLivelinessUrl(), other.getLivelinessUrl()))
            return false;

        if (!MirandaObject.stringsAreEqual(getName(), other.getName()))
            return false;

        if (!MirandaObject.stringsAreEqual(getTopic(), other.getTopic()))
            return false;

        if (!MirandaObject.stringsAreEqual(getOwner(), other.getOwner()))
            return false;

        return getErrorPolicy() == other.getErrorPolicy();
    }


    public void newEvent (Event event) {
        getEventQueue().SendNewEvent(getQueue(), this, event);
        if (isLocal()) {
            Miranda.getInstance().getDeliveryManager().sendDeliverEvent(event, this, getQueue(), this);
        }
    }

    @Override
    public void copyFrom(Mergeable mergeable) {
        try {
            Subscription other = (Subscription) mergeable;

            setOwner(other.getOwner());
            setName(other.getName());
            setDataUrl(other.getDataUrl());
            setErrorPolicy(other.getErrorPolicy());
            setLivelinessUrl(other.getLivelinessUrl());

            DeadLetterQueue deadLetterQueue = (DeadLetterQueue) other.getDeadLetterQueue().clone();
            setDeadLetterQueue(deadLetterQueue);

            setLocal(other.isLocal());
            setTopic(other.getTopic());

            EventQueue eventQueue = (EventQueue) other.getEventQueue().clone();
            setEventQueue(eventQueue);
        } catch (CloneNotSupportedException e) {
            Panic panic = new Panic("CloneNotSupportedException while executing copyFrom", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
        }

    }

    @Override
    public boolean merge(Mergeable other) {
        return false;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public long getLastChange() {
        return 0;
    }

    public void sendNewEvent(BlockingQueue<Message> senderQueue, Object senderObject, Event event) {
        NewEventMessage newEventMessage = new NewEventMessage(senderQueue, senderObject, null, event);
        sendToMe(newEventMessage);
    }
}
