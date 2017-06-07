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

package com.ltsllc.miranda.subsciptions;

import com.google.gson.Gson;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;

import java.io.Serializable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Subscription extends StatusObject<Subscription> implements Perishable, Serializable {
    public enum ErrorPolicies {
        Drop,
        Retry,
        Deadletter
    }
    private static Gson ourGson = new Gson();

    private long expires;
    private String name;
    private String owner;
    private String topic;
    private String dataUrl;
    private String livelinessUrl;
    private ErrorPolicies errorPolicy;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public Subscription () {
        super(Status.New);
    }

    public Subscription (String name) {
        this();
        this.name = name;
    }

    public Subscription (String name, String owner, String topic, String dataUrl, String livelinessUrl,
                         ErrorPolicies errorPolicy) {
        super(Status.New);

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

    public String getOwner() {
        return owner;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
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

    public boolean expired(long time) {
        boolean expired = super.expired();

        if (expired)
            return true;

        return 0 == expires || time > expires;
    }

    public String toJson() {
        return ourGson.toJson(this);
    }

    public void updateFrom (Subscription other) {
        super.updateFrom (other);

        setExpires(other.getExpires());
        setOwner(other.getOwner());
        setDataUrl(other.getDataUrl());
        setLivelinessUrl(other.getLivelinessUrl());
        setErrorPolicy(other.getErrorPolicy());
    }

    public boolean matches (Subscription other) {
        if (!super.matches(other))
            return false;

        return getName().equals(other.getName()) && getOwner().equals(other.getOwner());
    }
}
