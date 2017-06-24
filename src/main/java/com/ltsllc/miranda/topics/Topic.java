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

package com.ltsllc.miranda.topics;

import com.google.gson.Gson;
import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Topic extends StatusObject<Topic> implements Perishable {
    public enum RemotePolicies {
        None,
        Acknowledged,
        Written
    }


    private static Gson ourGson = new Gson();

    private String name;
    private String owner;
    private RemotePolicies remotePolicy;

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

    public Topic() {
        super(Status.New);
    }

    public Topic (String name) {
        super(Status.New);

        this.name = name;
    }

    public Topic (String name, String owner) {
        super(Status.New);

        this.name = name;
        this.owner = owner;
    }

    public void updateFrom (Topic other) {
        super.updateFrom(other);

        setOwner(other.getOwner());
        setRemotePolicy(other.getRemotePolicy());
    }

    public boolean matches (Topic other) {
        if (!super.matches(other))
            return false;

        return getName().equals(other.getName()) && getOwner().equals(other.getOwner());
    }

    public String toJson() {
        return ourGson.toJson(this);
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
            "hedi",
            "mushroom",
            "oscar",
            "clark"
    };

    public static Topic random (ImprovedRandom improvedRandom) {
        String name = NAMES[improvedRandom.nextIndex(NAMES)];
        String owner = OWNERS[improvedRandom.nextIndex(OWNERS)];

        return new Topic(name, owner);
    }
}
