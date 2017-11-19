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
import com.ltsllc.common.util.Utils;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An HTTP POST/PUT/DELETE that has been made to a topic.
 * <p>
 * <h3>Properties</h3>
 * <table border="1">
 * <th>
 * <td>Name</td>
 * <td>Type</td>
 * <td>Description</td>
 * </th>
 * <tr>
 * <td>guid</td>
 * <td>String</td>
 * <td>A string that identifies an Event from all the other events in the system.</td>
 * </tr>
 * <tr>
 * <td>method</td>
 * <td>enum, Methods</td>
 * <td>
 * The HTTP verb used for this instance.
 * Currently this is one of POST PUT or DELETE
 * </td>
 * </tr>
 * <tr>
 * <td>userName</td>
 * <td>String</td>
 * <td>The name of the User who created this Event.</td>
 * </tr>
 * <tr>
 * <td>timeOfCreation</td>
 * <td>long</td>
 * <td>The time returned by System.currentTimeMillis() when the Event was created.</td>
 * </tr>
 * <tr>
 * <td>topicName</td>
 * <td>String</td>
 * <td>The name of the Topic that this instance was published to.</td>
 * </tr>
 * <tr>
 * <td>awaitingDelivery</td>
 * <td>List of Objects</td>
 * <td>
 * <p>
 * The objects that are trying to deliver the Event.
 * </p>
 * <p>
 * Generally, clients do not interact with this property directly.
 * Instead, clients use methods like {@link #addAwaitingDelivery(Object)}}
 * or {@link #canBeEvicted()} to ascertain the state of this property.
 * </p>
 * <p>
 * If this is empty, then it is because the Event has just been created or when
 * everyone who was trying to deliver the Event has done so.
 * </p>
 * </td>
 * </tr>
 * <tr>
 * <td>content</td>
 * <td>byte[]</td>
 * <td>
 * <p>
 * The content of an Event.
 * </p>
 * <p>
 * The content of an Event is treated as a binary object even when the content is text ---
 * Miranda can't tell the difference.
 * </p>
 * <p>
 * This property can be null.
 * This is generally the case for HTTP DELETE operations.
 * </p>
 * <p>
 * In situations that an Event COULD have a content but does not,
 * for example an HTTP POST that has an empty body,
 * this property will generally be a zero length array.
 * </p>
 * </td>
 * </tr>
 * </table>
 */
public class Event extends MirandaObject implements DirectoryEntry, Evictable {
    private static SecureRandom random = new SecureRandom();

    public Event(ImprovedRandom random, byte[] content) {
        initialize(random);

        this.guid = UUID.randomUUID().toString();
        this.userName = random.randomString(8);
        this.method = randomMethods(random);
    }

    public enum Methods {
        Unknown,
        GET,
        POST,
        PUT,
        DELETE
    }

    private String guid;
    private byte[] content;
    private String userName;
    private long timeOfCreation;
    private Methods method;
    private String topicName;
    private List awaitingDelivery;

    public Event(Methods method, String hexString) throws IOException {
        byte[] content = Utils.hexStringToBytes(hexString);
        String guid = UUID.randomUUID().toString();

        basicConstructor(null, guid, null, System.currentTimeMillis(), method,
                content);
    }

    public Event(Methods method, byte[] buffer) {
        String guid = UUID.randomUUID().toString();
        basicConstructor(null, guid, null, System.currentTimeMillis(), method, buffer);
    }


    public Event(User user, Methods method, String topicName, byte[] content) {
        String guid = UUID.randomUUID().toString();
        basicConstructor(user.getName(), guid, topicName, System.currentTimeMillis(), method, content);
    }

    public Event(String userName, String guid, String topicName, long timeOfCreation, Methods method, byte[] content) {
        basicConstructor(userName, guid, topicName, timeOfCreation, method, content);
    }

    public void basicConstructor(String userName, String guid, String topicName, long timeOfCreation, Methods method,
                                 byte[] content) {

        this.userName = userName;
        this.guid = guid;
        this.topicName = topicName;
        this.timeOfCreation = timeOfCreation;
        this.method = method;
        this.content = content;

        this.awaitingDelivery = new ArrayList();
    }

    @Override
    public boolean isEquivalentTo(Object o) {
        if (o == null || !(o instanceof Event))
            return false;

        Event other = (Event) o;
        return stringsAreEqual(guid, other.guid);
    }

    @Override
    public void copyFrom(Mergeable mergeable) {
        Event other = (Event) mergeable;
        guid = other.guid;
        topicName = other.topicName;
        content = Utils.copy(other.content);
        userName = other.userName;
        timeOfCreation = other.timeOfCreation;
        method = other.method;
        awaitingDelivery = Utils.copy(other.awaitingDelivery);
    }

    public long getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(long timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setContent(byte[] content) {

        this.content = content;
    }

    public void addAwaitingDelivery(Object deliverer) {
        this.awaitingDelivery.add(deliverer);
    }

    /**
     * Is this Event the same as another Event?
     *
     * @param o The other Event
     * @return true if the Events are equivalent, false otherwise.
     */
    public boolean equals(Object o) {
        if (!super.equals(o))
            return false;

        Event other = (Event) o;

        if (!byteArraysAreEqual(getContent(), other.getContent()))
            return false;

        if (!stringsAreEqual(getGuid(), other.getGuid()))
            return false;

        if (getMethod() != other.getMethod())
            return false;

        if (!stringsAreEqual(getTopicName(), other.getTopicName()))
            return false;

        if (!stringsAreEqual(getUserName(), other.getUserName()))
            return false;

        return true;
    }

    private static Methods[] methods = {Methods.POST, Methods.GET, Methods.PUT, Methods.DELETE};

    /**
     * Create an Event with random attributes.
     *
     * @param random           The random source to use when choosing attributes.
     * @param maxSizeOfContent The max size (in bytes) of the event's content.  The actual
     *                         size will be a random value equal to or less than this.
     * @return a random Event
     */
    public static Event createRandom(ImprovedRandom random, int maxSizeOfContent) {
        int sizeOfContent = random.nextIndex(maxSizeOfContent);
        byte[] content = new byte[sizeOfContent];
        random.nextBytes(content);

        Event event = new Event(random, content);
        return event;
    }

    public static Methods randomMethods(ImprovedRandom random) {
        int index = random.nextIndex(methods.length);
        return methods[index];
    }

    public static Event createRandom() {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        return createRandom(improvedRandom, 1024);
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsHexString() {
        return Utils.bytesToString(getContent());
    }

    public String getGuid() {
        return guid;
    }

    public Methods getMethod() {
        return method;
    }

    public void setMethod(Methods method) {
        this.method = method;
    }


    public String toJson() {
        return getGson().toJson(this);
    }

    public void updateFrom(Event other) {
        throw new IllegalStateException("updateFrom is not applicable for Events");
    }

    public boolean matches(Event other) {
        return getGuid().equals(other.getGuid());
    }

    public String getKey() {
        return getGuid();
    }


    public boolean isEquivalentTo(DirectoryEntry other) {
        if (!(other instanceof Event))
            return false;

        Event otherEvent = (Event) other;
        return getGuid().equals(otherEvent.getGuid());
    }

    /**
     * Answer whether this instance is eligible for eviction.
     * <p>
     * <p>
     * An Event is eligible for eviction if no one is trying to deliver it.
     * </p>
     */
    public boolean canBeEvicted() {
        return awaitingDelivery.size() <= 0;
    }
}
