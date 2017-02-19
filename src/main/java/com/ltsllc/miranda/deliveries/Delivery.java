package com.ltsllc.miranda.deliveries;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Delivery implements Perishable {
    private static Gson ourGson = new Gson();

    private long expires;
    private String id;
    private String message;

    public Delivery () {

    }

    public boolean expired(long time) {
        return time > expires;
    }

    public String toJson() {
        return ourGson.toJson(this);
    }
}
