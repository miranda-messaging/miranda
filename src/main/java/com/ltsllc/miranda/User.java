package com.ltsllc.miranda;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class User extends StatusObject implements Perishable {
    private Gson ourGson = new Gson();

    private String name;
    private String description;
    private long expires;

    public User (String name, String desciption) {
        super(Status.New);

        this.name = name;
        this.description = desciption;
    }

    public User () {
        super(Status.New);
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
