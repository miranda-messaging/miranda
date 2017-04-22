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
    private Gson ourGson = new Gson();

    private long expires;
    private String name;
    private String owner;
    private String url;

    public Subscription () {
        super(Status.New);
    }

    public Subscription (String name) {
        this();
        this.name = name;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
    }

    public boolean matches (Subscription other) {
        if (!super.matches(other))
            return false;

        return getName().equals(other.getName()) && getOwner().equals(other.getOwner());
    }
}
