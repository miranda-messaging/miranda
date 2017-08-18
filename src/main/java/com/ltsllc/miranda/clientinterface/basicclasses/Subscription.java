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

import com.google.gson.Gson;

/**
 * A subscription to a topic.
 *
 * <h3>Attributes</h3>
 * <table border="1">
 *     <th>
 *         <td>Name</td>
 *         <td>Type</td>
 *         <td>Description</td>
 *     </th>
 *     <tr>
 *         <td>name</td>
 *         <td>String</td>
 *         <td>The name of the subscription.  This must be unique among all Subscriptions</td>
 *     </tr>
 *     <tr>
 *         <td>owner</td>
 *         <td>String</td>
 *         <td>The User that owns the Subscription.  This must refer to the name of an existing User.</td>
 *     </tr>
 *     <tr>
 *         <td>topic</td>
 *         <td>String</td>
 *         <td>The name of the Topic that the subscription is for.
 *             A topic name must refer to an existing Topic.</td>
 *     </tr>
 *     <tr>
 *         <td>dataUrl</td>
 *         <td>String</td>
 *         <td>The URL that the system will attempt to deliver Events to.</td>
 *     </tr>
 *     <tr>
 *         <td>livelinessUrl</td>
 *         <td>String</td>
 *         <td>The URL that the system will use to determine if the Subscription is online.
 *             If the system thinks that the Subscription is offline, it will do an
 *             HTTP GET against this URL to determine if it is online.  A successful result
 *             (a 200 code) indicates that the Subscription is online. Any other result and
 *             the system will conclude that the Subascription is offline.</td>
 *     </tr>
 *     <tr>
 *         <td>errorPolicy</td>
 *         <td>enum</td>
 *         <td>How the subscription handles error (non 200 code) results.
 *             The allowable values are Drop, Retry and DeadLetter.
 *
 *             <p>
 *                 When the system has an Event that it wants to deliver to a Subscription,
 *                 it tries to perform an HTTP operation against the Subscription's
 *                 dataUrl.  If the operation succeeds (the HTTP result is a 200 code),
 *                 then the system creates a new Delivery and removes the Event from the
 *                 Subscription's queue.  If the operation fails (a non 200 code),
 *                 the system consults the Suscription's errorPolicy.
 *             </p>
 *
 *             <p>
 *                 When a Subscription has an error policy of Drop,
 *                 a failure to deliver an Event results in that Event being discarded from the
 *                 Subscription's queue.
 *             </p>
 *
 *             <p>
 *                 When a Subscription has an error policy of Retry,
 *                 a failure to deliver an Event results in the system trying to deliver the
 *                 Event again.  Each time the system fails, it waits twice as long before attempting
 *                 a redelivery.  While retrying the Event, other Events will backup behind the retrying Event.
 *                 If a Subscription's queue gets longer than a configurable threshold,
 *                 new Events will be discarded in favor of old events.
 *             </p>
 *
 *             <p>
 *                 After a configurable number of tries, the system will discard the Event.
 *             </p>
 *
 *             <p>
 *                 When a Subscription has an error policy of DeadLetter,
 *                 it will put a failing Event into a "dead letter queue".
 *                 The max size of a dead letter queue is configurable.
 *                 When another Event needs to be added to a full dead letter queue,
 *                 it is discarded instead.
 *             </p>
 *         </td>
 *     </tr>
 * </table>
 */
public class Subscription extends MirandaObject {
    public enum ErrorPolicies {
        Drop,
        Retry,
        DeadLetter
    }

    private String name;
    private String owner;
    private String topic;
    private String dataUrl;
    private String livelinessUrl;
    private ErrorPolicies errorPolicy;

    @Override
    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof Subscription))
            return false;

        Subscription other = (Subscription) o;
        if (!(stringsAreEqual(owner, other.owner)))
            return false;

        if (!(stringsAreEqual(dataUrl, other.dataUrl)))
            return false;

        return stringsAreEqual(livelinessUrl, other.livelinessUrl);
    }

    @Override
    public void copyFrom(Mergeable mergeable) {
        Subscription other = (Subscription) mergeable;

        name = other.name;
        owner = other.owner;
        topic = other.topic;
        dataUrl = other.dataUrl;
        livelinessUrl = other.livelinessUrl;
        errorPolicy = other.errorPolicy;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public Subscription () {
    }

    public Subscription (String name) {
        this();
        this.name = name;
    }

    public Subscription (String name, String owner, String topic, String dataUrl, String livelinessUrl,
                         ErrorPolicies errorPolicy) {
        this.name = name;
        this.owner = owner;
        this.topic = topic;
        this.dataUrl = dataUrl;
        this.livelinessUrl = livelinessUrl;
        this.errorPolicy = errorPolicy;
    }

    public ErrorPolicies getErrorPolicy() {
        return errorPolicy;
    }

    public void setErrorPolicy(ErrorPolicies errorPolicy) {
        this.errorPolicy = errorPolicy;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLivelinessUrl() {
        return livelinessUrl;
    }

    public void setLivelinessUrl(String livelinessUrl) {
        this.livelinessUrl = livelinessUrl;
    }

    public String getDataUrl() {

        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public void updateFrom (Subscription other) {
        setOwner(other.getOwner());
        setDataUrl(other.getDataUrl());
        setLivelinessUrl(other.getLivelinessUrl());
        setErrorPolicy(other.getErrorPolicy());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!super.equals(o))
            return false;

        Subscription other = (Subscription) o;

        if (!stringsAreEqual(getDataUrl(), other.getDataUrl()))
            return false;

        if (!stringsAreEqual(getLivelinessUrl(), other.getLivelinessUrl()))
            return false;

        if (!stringsAreEqual(getName(), other.getName()))
            return false;

        if (!stringsAreEqual(getTopic(), other.getTopic()))
            return false;

        if (!stringsAreEqual(getOwner(), other.getOwner()))
            return false;

        if (getErrorPolicy() != other.getErrorPolicy())
            return false;

        return true;
    }
}
