package com.ltsllc.miranda.subscriptionInfo;

import com.ltsllc.miranda.directory.DirectoryEntry;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Updateable;

/**
 * An {@link com.ltsllc.miranda.event.Event} and when (if) it was delivered ({@link com.ltsllc.miranda.deliveries.Delivery}).
 *
 * This class represents an Event and, if it has been delivered, when it was Delivered.
 * A subscription has one of these for each Event of interest to the Subscription.
 *
 * If the Delivery for the instance is null, then the event hasn't been delivered yet.
 *
 * <h3>PROPERTIES</h3>
 * <ul>
 *     <li>event - The GUID of the Event that this instance pertains to.
 *     This property should always contain a meaningful value.</li>
 *     <li>delivery - The GUID of the Delivery for this Event.  A null value
 *     signifies that the Event hasn't been delivered yet.</li>
 * </ul>
 */
public class SubscriptionEntry implements DirectoryEntry, Updateable<SubscriptionEntry>, Matchable<SubscriptionEntry> {
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

    public void setEvent (String guid) {
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
    @Override
    public boolean matches(SubscriptionEntry other) {
        return isEquivalentTo(other);
    }
}
