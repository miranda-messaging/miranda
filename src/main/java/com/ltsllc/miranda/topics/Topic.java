package com.ltsllc.miranda.topics;

import com.google.gson.Gson;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Topic extends StatusObject implements Perishable {
    private Gson ourGson = new Gson();

    private long expiration;
    private String name;

    public Topic() {
        super(Status.New);
    }

    public Topic (String name) {
        super(Status.New);

        this.name = name;
        this.expiration = Long.MAX_VALUE;
    }

    public long getExpiration() {
        return expiration;
    }

    public boolean expired (long time) {
        boolean expired = super.expired();

        if (expired)
            return true;

        return 0 == expiration || time > expiration;
    }


    public String toJson() {
        return ourGson.toJson(this);
    }
}
