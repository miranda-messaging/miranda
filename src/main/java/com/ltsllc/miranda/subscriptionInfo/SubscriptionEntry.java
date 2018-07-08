package com.ltsllc.miranda.subscriptionInfo;

import com.ltsllc.miranda.clientinterface.basicclasses.*;
import com.ltsllc.miranda.file.Updateable;

/**
 * An {@link Event} and when (if) it was delivered ({@link com.ltsllc.miranda.clientinterface.basicclasses.Delivery}).
 * <p>
 * This class represents an Event and, if it has been delivered, when it was delivered.
 * A subscription has one of these for each Event of interest to the Subscription.
 * <p>
 * If the Delivery for the instance is null, then the event hasn't been delivered yet.
 * <p>
 * <h3>PROPERTIES</h3>
 * <ul>
 * <li>event - The GUID of the Event that this instance pertains to.
 * This property should always contain a meaningful value.</li>
 * <li>delivery - The GUID of the Delivery for this Event.  A null value
 * signifies that the Event hasn't been delivered yet.</li>
 * </ul>
 */
public class SubscriptionEntry extends MergeableObject implements DirectoryEntry, Updateable<SubscriptionEntry>, Equivalent
{
    private String event;
    private String delivery;

    /**
     * Create a new instance.
     *
     * @param event The GUID of the event that this instance pertains to.
     */
    public SubscriptionEntry(String event) {
        this.event = event;
    }

    /**
     * Create a new instance.
     *
     * @param event The event that this instance pertains to.  This parameter
     *              should be non-null.
     */
    public SubscriptionEntry(Event event) {
        this.event = event.getGuid();
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String guid) {
        this.event = guid;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    /**
     * Is the other instance's entity guid the same as ours?
     *
     * @see DirectoryEntry#isEquivalentTo(DirectoryEntry)
     */
    @Override
    public boolean isEquivalentTo(DirectoryEntry other) {
        SubscriptionEntry otherSubsciptionEntry = (SubscriptionEntry) other;

        return getEvent().equals(otherSubsciptionEntry.getEvent());
    }

    /**
     * Return the guid for our Event.
     *
     * @see DirectoryEntry#getKey()
     */
    @Override
    public String getKey() {
        return null;
    }

    /**
     * Make this instance the same as another instance.
     *
     * @see Updateable#updateFrom(Object)
     */
    @Override
    public void updateFrom(SubscriptionEntry other) {
        setEvent(other.getEvent());
        setDelivery(other.getDelivery());
    }

    /**
     * Is this instance equivalent to another instance?
     *
     * @see SubscriptionEntry#isEquivalentTo(DirectoryEntry)
     */

    public boolean isEquivalentTo(Mergeable o) {
        if (null == o)
            return false;

        if (!(o instanceof SubscriptionEntry))
            return false;

        Mergeable other = (Mergeable) o;

        return isEquivalentTo(other);
    }

    public void copyFrom(Mergeable mergeable) {
        SubscriptionEntry other = (SubscriptionEntry) mergeable;
        setEvent(other.getEvent());
        setDelivery(other.getDelivery());
    }

    public boolean merge (Mergeable mergeable) {
        SubscriptionEntry other = (SubscriptionEntry) mergeable;

        if (getLastChange() > other.getLastChange())
            return false;
        else {
            copyFrom(other);
            return true;
        }
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (!(o instanceof SubscriptionEntry))
            return false;

        SubscriptionEntry other = (SubscriptionEntry) o;
        if (!getEvent().equals(other.getEvent()))
            return false;

       return getDelivery().equals(other.getDelivery());

    }

    @Override
    public void copyFrom(MergeableObject mergeable) {
        SubscriptionEntry other = (SubscriptionEntry) mergeable;

        setDelivery(other.getDelivery());
        setEvent(other.getEvent());

    }
}
