package com.ltsllc.miranda.clientinterface.basicclasses;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.deliveries.DeliveryAttempt;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.eventqueue.states.EventQueueReadyState;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A queue of GUIDs that represent the events that haven't been delivered yet.
 */
public class EventQueue extends Consumer implements Cloneable, Mergeable, Equivalent {
    private transient Subscription subscription;
    private List<String> events = new LinkedList();
    private String subscriptionName;
    private DeliveryAttempt lastAttempt = null;

    public DeliveryAttempt getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(DeliveryAttempt lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
        setSubscriptionName(subscription.getName());
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public EventQueue (String filename) {
        super (filename, new LinkedBlockingQueue<Message>());
        setCurrentState(new EventQueueReadyState(this));
    }

    public EventQueue (Subscription subscription) {
        super(subscription.getName() + "(queue)", new LinkedBlockingQueue<Message>());

        setCurrentState(new EventQueueReadyState(this));
        setSubscription(subscription);
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (!(o instanceof EventQueue)) {
            return false;
        }

        if (o == null) {
            return false;
        }

        EventQueue other = (EventQueue) o;

        if (!(getSubscriptionName().equals(other.getSubscriptionName()))) {
            return false;
        }

        if (other.getEvents().size() != getEvents().size()) {
            return false;
        }

        List<String> otherEvents = other.getEvents();
        for (int i = 0; i < getEvents().size(); i++) {
            if (!MirandaObject.stringsAreEqual(getEvents().get(i), otherEvents.get(i)))
                return false;
        }

        return true;
    }

    public void newEvent (Event event) {
        if (getLastAttempt() == null) {
            DeliveryAttempt deliveryAttempt = new DeliveryAttempt(event);
            setLastAttempt(deliveryAttempt);
        }

        getEvents().add(event.getGuid());
        String filename = Miranda.properties.getProperty(MirandaProperties.PROPERTY_EVENT_QUEUE_DIRECTORY)
                + File.separator + getSubscription().getName() + ".queue";

        Miranda.getInstance().getWriter().sendWrite(getQueue(), this, filename, toJson().getBytes());
    }

    public void copyFrom (Mergeable mergeable) {
        EventQueue other = (EventQueue) mergeable;

        List<String> newEvents = new LinkedList<>(other.events);
        setEvents(newEvents);

        setSubscription(other.getSubscription());

        if (null == getLastAttempt())
            setLastAttempt(null);
        else
            setLastAttempt(new DeliveryAttempt(other.getLastAttempt()));
    }

    public boolean isEquivalentTo (Mergeable object) {
        EventQueue other = (EventQueue) object;

        if (!MirandaObject.listsAreEqual(getEvents(), other.getEvents()))
            return false;

        if (!getSubscription().isEquivalentTo(other.getSubscription()))
            return false;

        if (getLastAttempt() != other.getLastAttempt())
            return false;

        if (!MirandaObject.stringsAreEqual(getSubscriptionName(), other.getSubscriptionName()))
            return false;


        return true;
    }

    public String toJson () {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(this);
        return json;
    }

    @Override
    public boolean merge(Mergeable mergeable) {
        EventQueue other = (EventQueue) mergeable;
        if (getLastChange() > other.getLastChange())
            return false;
        else {
            copyFrom(other);
            return true;
        }
    }

    public Object clone () throws CloneNotSupportedException {
        return super.clone();
    }

    public void sendNewEvent(BlockingQueue<Message> senderQueue, Object senderObject, Event event) {
        NewEventMessage newEventMessage = new NewEventMessage(senderQueue, senderObject, null, event);
        sendToMe(newEventMessage);
    }

    public byte[] getData() {
        String json = getGson().toJson(getEvents());
        return json.getBytes();
    }

    public void rectify () {
        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        setQueue(queue);
        EventQueueReadyState state = new EventQueueReadyState(this);
        setCurrentState(state);
        Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionMessage(getQueue(), this, getSubscriptionName());
    }
}
