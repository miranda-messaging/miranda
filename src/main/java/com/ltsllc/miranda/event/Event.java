package com.ltsllc.miranda.event;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

import java.util.UUID;

/**
 * Created by Clark on 2/19/2017.
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

    public boolean expired (long time) {
        return false;
    }

    public String toJson () {
        return ourGson.toJson(this);
    }
}
