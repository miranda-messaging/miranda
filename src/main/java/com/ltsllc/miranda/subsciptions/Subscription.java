package com.ltsllc.miranda.subsciptions;

import com.google.gson.Gson;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.Updateable;

import java.io.Serializable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Subscription extends StatusObject<Subscription> implements Perishable, Serializable {
    public enum RemotePolicies {
        None,
        AcknowledgeReceipt,
        Written
    }
    private static Gson ourGson = new Gson();

    private long expires;
    private String name;
    private String owner;
    private String topic;
    private String dataUrl;
    private String livelinessUrl;
    private RemotePolicies remotePolicy;

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

    public RemotePolicies getRemotePolicy() {
        return remotePolicy;
    }

    public void setRemotePolicy(RemotePolicies remotePolicy) {
        this.remotePolicy = remotePolicy;
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
        setRemotePolicy(other.getRemotePolicy());
    }

    public boolean matches (Subscription other) {
        if (!super.matches(other))
            return false;

        return getName().equals(other.getName()) && getOwner().equals(other.getOwner());
    }
}
