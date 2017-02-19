package com.ltsllc.miranda;

import com.ltsllc.miranda.file.Perishable;

/**
 * Created by Clark on 1/5/2017.
 */
public class Delivery implements Perishable {
    private long expires;

    public boolean expired(long time) {
        return time > expires;
    }
}
