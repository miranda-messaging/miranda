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
import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.Updateable;
import com.ltsllc.miranda.util.ImprovedRandom;
import com.ltsllc.miranda.util.Utils;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by Clark on 2/19/2017.
 */

/**
 * A message that was sent to a topic.
 */
public class Event implements Perishable, Updateable<Event>, Matchable<Event> {
    private static Gson ourGson = new Gson();
    private static SecureRandom random = new SecureRandom();

    public enum Methods {
        GET,
        POST,
        PUT,
        DELETE
    }

    private String id;
    private String content;
    private Methods method;
    private long created;


    public Event (Methods method, String content) {
        this.method = method;
        this.content = content;
        this.created = System.currentTimeMillis();

        this.id = UUID.randomUUID().toString();
    }

    public Event (Methods method, byte[] buffer) {
        this.method = method;

        String hexString = Utils.bytesToString(buffer);
        this.content = hexString;

        this.created = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
    }

    public Event (String eventId, Methods method, String content, long time) {
        this.id = eventId;
        this.method = method;
        this.content = content;
        this.created = time;
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

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
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
        return getId().equals(other.getId());
    }
}
