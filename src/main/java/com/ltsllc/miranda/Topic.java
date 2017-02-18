package com.ltsllc.miranda;

import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Topic implements Perishable {
    private long expiration;
    private String name;

    public Topic() {
    }

    public Topic (String name) {
        this.name = name;
        this.expiration = Long.MAX_VALUE;
    }

    public long getExpiration() {
        return expiration;
    }

    public boolean expired (long time) {
        return 0 == expiration || time > expiration;
    }
}
