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

import com.ltsllc.commons.util.ImprovedRandom;
import com.ltsllc.miranda.clientinterface.basicclasses.Delivery;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.clientinterface.test.TestCase;
import org.junit.Test;

import java.io.IOException;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestDelivery extends TestCase {
    private Delivery delivery;

    public Delivery getDelivery() {
        return delivery;
    }


    public void setup() {
        try {
            reset();

            super.setup();

            Event event = new Event(Event.Methods.POST, "010203");
            Subscription subscription = new Subscription();

            this.delivery = new Delivery(event, System.currentTimeMillis(), subscription);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor() {
        try {
            Event event = new Event(Event.Methods.POST, "010203");
            Subscription subscription = new Subscription();

            long timeDelivered = System.currentTimeMillis();

            this.delivery = new Delivery(event, timeDelivered, subscription);

            assert (getDelivery().getEventGuid().equals(event.getGuid()));
            assert (getDelivery().getDelivered() == timeDelivered);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMerge () {
        ImprovedRandom improvedRandom = new ImprovedRandom();

        Delivery delivery = Delivery.createRandomDelivery(improvedRandom);
        delivery.setDelivered(System.currentTimeMillis());
        delivery.setLastChange(System.currentTimeMillis());

        pause(1);

        Delivery update = Delivery.createRandomDelivery(improvedRandom);
        update.setDelivered(System.currentTimeMillis());
        update.setLastChange(System.currentTimeMillis());
        IllegalStateException illegalStateException = null;

        try {
            delivery.merge(update);
        } catch (IllegalStateException e) {
            illegalStateException = e;
        }

        assert (null == illegalStateException);
    }

    @Test
    public void testMatch() {
        ImprovedRandom improvedRandom = new ImprovedRandom();

        Delivery delivery = Delivery.createRandomDelivery(improvedRandom);
        Delivery other = Delivery.createRandomDelivery(improvedRandom);

        assert (delivery.isEquivalentTo(delivery));
        assert (!delivery.isEquivalentTo(other));
    }

    @Test
    public void testCopyFrom () throws IOException {
        Delivery original = new Delivery("high there", "low there", 1, "test subscription");
        Delivery delivery = new Delivery("high there","low there",1,"test subscription");
        Delivery delivery2 = new Delivery("delivery2", "whatever", 2, "whatever2");
        assert (original.isEquivalentTo(delivery));
        delivery.copyFrom(delivery2);
        assert (delivery.isEquivalentTo(delivery2));
        assert (!delivery.isEquivalentTo(original));
    }
}

