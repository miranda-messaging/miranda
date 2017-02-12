package com.ltsllc.miranda;

import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Topic implements Perishable {
    private long expiration;

    public Topic() {
    }

    public long getExpiration() {
        return expiration;
    }

    public boolean expired (long time) {
        return 0 == expiration || time > expiration;
    }
}
