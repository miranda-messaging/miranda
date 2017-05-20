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

package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.subsciptions.Subscription;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
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

        super.setup();

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

    @Test
    public void testUpdateFrom () {
        Delivery delivery = Delivery.createRandomDelivery();
        Delivery update = Delivery.createRandomDelivery();
        IllegalStateException illegalStateException = null;

        try {
            delivery.updateFrom(update);
        } catch (IllegalStateException e) {
            illegalStateException = e;
        }

        assert (null != illegalStateException);
    }

    @Test
    public void testMatch () {
        Delivery delivery = Delivery.createRandomDelivery();
        Delivery other = Delivery.createRandomDelivery();

        assert (delivery.matches(delivery));
        assert (!delivery.matches(other));
    }
}

