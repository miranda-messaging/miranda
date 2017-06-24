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

package com.ltsllc.miranda.event;

import com.google.gson.Gson;
import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.directory.DirectoryEntry;
import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.Updateable;
import com.ltsllc.miranda.user.User;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Clark on 2/19/2017.
 */

/**
 * An HTTP POST/PUT/DELETE that has been made to a topic.
 *
 * <h3>Properties</h3>
 * <table border="1">
 *     <th>
 *         <td>Name</td>
 *         <td>Type</td>
 *         <td>Description</td>
 *     </th>
 *     <tr>
 *         <td>guid</td>
 *         <td>String</td>
 *         <td>A string that identifies an Event from all the other events in the system.</td>
 *     </tr>
 *     <tr>
 *         <td>method</td>
 *         <td>enum, Methods</td>
 *         <td>
 *             The HTTP verb used for this instance.
 *             Currently this is one of POST PUT or DELETE
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>userName</td>
 *         <td>String</td>
 *         <td>The name of the User who created this Event.</td>
 *     </tr>
 *     <tr>
 *         <td>timeOfCreation</td>
 *         <td>long</td>
 *         <td>The time returned by System.currentTimeMillis() when the Event was created.</td>
 *     </tr>
 *     <tr>
 *         <td>topicName</td>
 *         <td>String</td>
 *         <td>The name of the Topic that this instance was published to.</td>
 *     </tr>
 *     <tr>
 *         <td>awaitingDelivery</td>
 *         <td>List of Objects</td>
 *         <td>
 *             <p>
 *             The objects that are trying to deliver the Event.
 *             </p>
 *             <p>
 *             Generally, clients do not interact with this property directly.
 *             Instead, clients use methods like {@link #addAwaitingDelivery(Object)}}
 *             or {@link #canBeEvicted()} to ascertain the state of this property.
 *             </p>
 *             <p>
 *                 If this is empty, then it is because the Event has just been created or when
 *                 everyone who was trying to deliver the Event has done so.
 *             </p>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>content</td>
 *         <td>byte[]</td>
 *         <td>
 *             <p>
 *                 The content of an Event.
 *             </p>
 *             <p>
 *                 The content of an Event is treated as a binary object even when the content is text ---
 *                 Miranda can't tell the difference.
 *             </p>
 *             <p>
 *                 This property can be null.
 *                 This is generally the case for HTTP DELETE operations.
 *             </p>
 *             <p>
 *                 In situations that an Event COULD have a content but does not,
 *                 for example an HTTP POST that has an empty body,
 *                 this property will generally be a zero length array.
 *             </p>
 *         </td>
 *     </tr>
 * </table>
 */
public class Event implements Perishable, Updateable<Event>, Matchable<Event>, DirectoryEntry {
    private static Gson ourGson = new Gson();
    private static SecureRandom random = new SecureRandom();

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

    public Event (Methods method, String hexString) throws IOException {
        byte[] content = Utils.hexStringToBytes(hexString);
        String guid = UUID.randomUUID().toString();

        basicConstructor(null, guid, null, System.currentTimeMillis(), method,
                content);
    }

    public Event (Methods method, byte[] buffer) {
        String guid = UUID.randomUUID().toString();
        basicConstructor(null, guid, null, System.currentTimeMillis(), method, buffer);
    }


    public Event (User user, Methods method, String topicName, byte[] content) {
        String guid = UUID.randomUUID().toString();
        basicConstructor(user.getName(), guid, topicName, System.currentTimeMillis(), method, content);
    }

    public Event (String userName, String guid, String topicName, long timeOfCreation, Methods method, byte[] content) {
        basicConstructor(userName, guid, topicName, timeOfCreation, method, content);
    }

    public void basicConstructor (String userName, String guid, String topicName, long timeOfCreation, Methods method,
                                  byte[] content) {

        this.userName = userName;
        this.guid = guid;
        this.topicName = topicName;
        this.timeOfCreation = timeOfCreation;
        this.method = method;
        this.content = content;

        this.awaitingDelivery = new ArrayList();
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

    public void addAwaitingDelivery (Object deliverer) {
        this.awaitingDelivery.add(deliverer);
    }


    private static Methods[] methods = {Methods.POST, Methods.GET, Methods.PUT, Methods.DELETE};

    public static Methods randomMethods (ImprovedRandom random) {
        int index = random.nextIndex(methods.length);
        return methods[index];
    }


    public static Event createRandom (ImprovedRandom random, int maxSizeOfContent) {
        int sizeOfContent = random.nextIndex(maxSizeOfContent);
        byte[] content = new byte[sizeOfContent];
        random.nextBytes(content);

        return new Event(randomMethods(random),content);
    }

    public static Event createRandom () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        return createRandom(improvedRandom, 1024);
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsHexString () {
        return Utils.bytesToString(getContent());
    }

    public String getGuid() {
        return guid;
    }

    public Methods getMethod() {
        return method;
    }

    public void setMethod (Methods method) {
        this.method = method;
    }

    /**
     * An Event never expires
     *
     * @param time The time to compare with
     * @return if the {@link Perishable} has expired.  In the case of this
     * class, always return false.
     */
    public boolean expired (long time) {
        return false;
    }

    public String toJson () {
        return ourGson.toJson(this);
    }

    public void updateFrom (Event other) {
        throw new IllegalStateException("updateFrom is not applicable for Events");
    }

    public boolean matches (Event other) {
        return getGuid().equals(other.getGuid());
    }

    public String getKey () {
        return getGuid();
    }

    @Override
    public boolean isEquivalentTo(DirectoryEntry other) {
        if (!(other instanceof Event))
            return false;

        Event otherEvent = (Event) other;
        return getGuid().equals(otherEvent.getGuid());
    }

    /**
     * Answer whether this instance is eligible for eviction.
     *
     * <p>
     *     An Event is eligible for eviction if no one is trying to deliver it.
     * </p>
     */
    public boolean canBeEvicted () {
        return awaitingDelivery.size() <= 0;
    }
}
