package com.ltsllc.miranda.event;

import com.google.gson.Gson;
import com.ltsllc.miranda.Utils;
import com.ltsllc.miranda.file.Perishable;

import java.util.UUID;

/**
 * Created by Clark on 2/19/2017.
 */

/**
 * A message that was sent to a topic.
 */
public class Event implements Perishable {
    private Gson ourGson = new Gson();

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
}
