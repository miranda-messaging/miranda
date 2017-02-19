package com.ltsllc.miranda;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Subscription extends StatusObject implements Perishable {
    private Gson ourGson = new Gson();

    private long expires;
    private String name;

    public Subscription () {
        super(Status.New);
    }

    public String getName() {
        return name;
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
}
