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

import com.ltsllc.common.util.ImprovedRandom;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by Clark on 1/5/2017.
 */

/**
 * A successful delivery of an {@link Event} to a {@link Subscription}.
 */
public class Delivery extends MirandaObject {
    private static SecureRandom ourRandom = new SecureRandom();

    private String eventGuid;
    private String attemptId;
    private long delivered;
    private String subscription;

    public Delivery(Event event, long delivered, Subscription subscription) {
        this.attemptId = UUID.randomUUID().toString();
        this.eventGuid = event.getGuid();
        this.delivered = delivered;
        this.subscription = subscription.getName();
    }

    public Delivery(String deliveryId, String eventId, long time, String subscription) {
        this.eventGuid = deliveryId;
        this.attemptId = eventId;
        this.delivered = time;
        this.subscription = subscription;
    }

    public Delivery(ImprovedRandom random) {
        initialize(random);

        this.eventGuid = UUID.randomUUID().toString();
        this.attemptId = UUID.randomUUID().toString();
        this.delivered = random.nextNonNegativeLong();
        this.subscription = random.randomString(16);
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof Delivery))
            return false;

        Delivery other = (Delivery) o;

        return eventGuid.equals(other.eventGuid);
    }

    @Override
    public void copyFrom(Mergeable mergeable) {
        Delivery other = (Delivery) mergeable;

        eventGuid = other.eventGuid;
        attemptId = other.attemptId;
        delivered = other.delivered;
        subscription = other.subscription;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public long getDelivered() {
        return delivered;
    }

    public String getEventGuid() {
        return eventGuid;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setEventGuid(String eventGuid) {
        this.eventGuid = eventGuid;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public void setDelivered(long delivered) {
        this.delivered = delivered;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public static Delivery createRandomDelivery(ImprovedRandom random) {
        String guid = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();
        long deliveryTime = ourRandom.nextLong();
        String subscriptionId = UUID.randomUUID().toString();

        Delivery delivery = new Delivery(random);
        return delivery;
    }
}
