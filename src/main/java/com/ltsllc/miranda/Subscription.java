package com.ltsllc.miranda;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Subscription implements Perishable {
    private Gson ourGson = new Gson();

    private long expires;

    public boolean expired(long time) {
        return 0 == expires || time > expires;
    }

    public String toJson() {
        return ourGson.toJson(this);
    }
}
