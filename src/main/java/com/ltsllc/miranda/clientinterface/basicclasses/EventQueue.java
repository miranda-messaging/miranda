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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A queue of GUIDs that represent the events that haven't been delivered yet.
 */
public class EventQueue extends Consumer implements Cloneable, Mergeable, Equivalent {
    private transient Subscription subscription;
    private List<String> events = new LinkedList();
    private String subscriptionName;
    private Map<String, DeliveryAttempt> deliveryAttemptMap = new HashMap<>();
    private Event currentEvent;

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
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

        for (int i = 0; i < getEvents().size(); i++) {
            String myEvent = getEvents().get (i);
            String theirEvent = other.getEvents().get(i);

            if (!MirandaObject.stringsAreEqual(myEvent, theirEvent))
                return false;
        }

        return true;
    }

    public void newEvent (Event event) {
        String eventId = event.getGuid();
        getEvents().add(eventId);
        String filename = Miranda.properties.getProperty(MirandaProperties.PROPERTY_EVENT_QUEUE_DIRECTORY)
                + File.separator + getSubscription().getName() + ".queue";

        Miranda.getInstance().getWriter().sendWrite(getQueue(), this, filename, toJson().getBytes());
    }

    public void copyFrom (Mergeable mergeable) {
        EventQueue other = (EventQueue) mergeable;

        List<String> newEvents = new LinkedList<>(other.events);
    }

    public boolean isEquivalentTo (Mergeable object) {
        EventQueue other = (EventQueue) object;
        return MirandaObject.listsAreEqual(getEvents(), other.getEvents());
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

    public DeliveryAttempt getDeliveryAttemptFor (Event event) {
        DeliveryAttempt deliveryAttempt = deliveryAttemptMap.get(event.getGuid());
        if (null == deliveryAttempt) {
            deliveryAttempt = new DeliveryAttempt(event);
            deliveryAttemptMap.put (event.getGuid(), deliveryAttempt);
        }

        return deliveryAttempt;
    }

    public void incrementNumberOfTries (Event event) {
        DeliveryAttempt deliveryAttempt = getDeliveryAttemptFor(event);
        deliveryAttempt.incrementTries();
    }

    public long getTimeOfNextTry(Event event) {
        DeliveryAttempt deliveryAttempt = getDeliveryAttemptFor(event);
        return deliveryAttempt.getTime();
    }

    public void advance ()
    {
        if (getEvents().size() > 0) {
            String theCurrentEventId = getEvents().remove(0);
            Miranda.getInstance().getEventManager().sendGetEvent(theCurrentEventId, getQueue(), this);
        }
    }
}
