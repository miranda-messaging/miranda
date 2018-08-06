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

import com.ltsllc.commons.util.ImprovedRandom;
import com.ltsllc.miranda.clientinterface.basicclasses.Delivery;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
    }

    @Test
    public void testConstructor() {
    }

    @Test
    public void testMerge () {
        ImprovedRandom improvedRandom = new ImprovedRandom();

        Delivery delivery = Delivery.createRandomDelivery(improvedRandom);
        Delivery update = Delivery.createRandomDelivery(improvedRandom);

        IllegalStateException illegalStateException = null;

        try {
            delivery.merge(update);
        } catch (IllegalStateException e) {
            illegalStateException = e;
        }

        assert (null == illegalStateException);
    }

    @Test
    public void testEquals () {
        ImprovedRandom improvedRandom = new ImprovedRandom();

        Delivery delivery = Delivery.createRandomDelivery(improvedRandom);
        Delivery other = Delivery.createRandomDelivery(improvedRandom);

        assert (delivery.equals(delivery));
        assert (!delivery.equals(other));
    }
}

