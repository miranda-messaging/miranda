package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.Subscription;
import com.ltsllc.miranda.event.Event;
import org.junit.Before;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Test;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestDelivery extends TestCase {
    private Delivery delivery;

    public Delivery getDelivery() {
        return delivery;
    }

    @Before
    public void setup() {
        reset();

        Event event = new Event(Event.Methods.POST, "junk");
        Subscription subscription = new Subscription();

        this.delivery = new Delivery(event, System.currentTimeMillis(), subscription);
    }

    @Test
    public void testConstructor() {
        Event event = new Event(Event.Methods.POST, "junk");
        Subscription subscription = new Subscription();

        long timeDelivered = System.currentTimeMillis();

        this.delivery = new Delivery(event, timeDelivered, subscription);

        assert (getDelivery().getMessageId().equals(event.getId()));
        assert (getDelivery().getDelivered() == timeDelivered);
    }
}

