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
import com.ltsllc.miranda.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * A topic in the Miranda System
 */
public class Topic extends MirandaObject {

    public enum RemotePolicies {
        Immediate,
        Acknowledged,
        Written
    }

    private String name;
    private String owner;
    private RemotePolicies remotePolicy;
    private List<Subscription> subscriptionList = new ArrayList<>();


    public List<Subscription> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(List<Subscription> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof Topic))
            return false;

        Topic other = (Topic) o;

        if (!listsAreEqual(getSubscriptionList(), other.getSubscriptionList()))
            return false;

        return stringsAreEqual(name, other.name);
    }

    @Override
    public void copyFrom(MergeableObject mergeable) {
        Topic other = (Topic) mergeable;

        name = other.name;
        owner = other.owner;
        remotePolicy = other.remotePolicy;
        subscriptionList = new ArrayList<>(other.getSubscriptionList());
    }

    public RemotePolicies getRemotePolicy() {
        return remotePolicy;
    }

    public void setRemotePolicy(RemotePolicies remotePolicy) {
        this.remotePolicy = remotePolicy;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Topic(String name) {
        this.name = name;
    }

    public Topic(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    public void updateFrom(Topic other) {
        setOwner(other.getOwner());
        setRemotePolicy(other.getRemotePolicy());
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o))
            return false;

        Topic other = (Topic) o;

        if (!stringsAreEqual(getName(), other.getName()))
            return false;

        if (!stringsAreEqual(getOwner(), other.getOwner()))
            return false;

        if (getRemotePolicy() != other.getRemotePolicy())
            return false;

        return true;
    }

    private static final String[] NAMES = {
            "whatever",
            "users",
            "subjects",
            "books",
            "people",
            "cars",
            "locations",
            "cats",
            "dogs",
            "gerbils"
    };

    private static final String[] OWNERS = {
            "whatever",
            "joe",
            "princess",
            "arnold",
            "sam",
            "steve",
            "heidi",
            "mushroom",
            "oscar",
            "clark"
    };

    public static Topic random(ImprovedRandom improvedRandom) {
        String name = NAMES[improvedRandom.nextIndex(NAMES)];
        String owner = OWNERS[improvedRandom.nextIndex(OWNERS)];

        return new Topic(name, owner);
    }

    /**
     * A new {@link Event} came in, process it.
     *
     * <p>
     *     Note that the event may not pertain to us; in which case it will be ignored.
     * </p>
     *
     * @param event The new Event
     */
    public void newEvent(BlockingQueue<Message> queue, Event event) {
        if (getName().equals (event.getTopicName())) {
            for (Subscription subscription : getSubscriptionList()) {
                subscription.newEvent(queue, event);
            }
        }
    }

    public void addSubscription(Subscription subscription) {
        getSubscriptionList().add(subscription);
    }

    @Override
    public void copyFrom(Mergeable mergeable) {
        Topic other = (Topic) mergeable;

        setName(other.getName());
        setOwner(other.getOwner());
        setRemotePolicy(other.getRemotePolicy());

        List subscriptions = new ArrayList(other.getSubscriptionList());
        setSubscriptionList(subscriptions);
        setLastChange(other.getLastChange());
    }

    @Override
    public boolean merge(Mergeable mergeable) {
        Topic other = (Topic) mergeable;

        if (getLastChange() > other.getLastChange())
            return false;
        else {
            copyFrom(other);
            return true;
        }
    }
}
