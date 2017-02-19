package com.ltsllc.miranda;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class User implements Perishable {
    private Gson ourGson = new Gson();

    private String name;
    private String description;
    private long expires;

    public User (String name, String desciption) {
        this.name = name;
        this.description = desciption;
    }

    public User () {
    }

    public boolean expired(long time) {
        return 0 == expires || time > expires;
    }

    public String toJson() {
        return ourGson.toJson(this);
    }
}
