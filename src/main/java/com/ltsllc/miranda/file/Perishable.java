package com.ltsllc.miranda.file;

/**
 * Created by Clark on 2/12/2017.
 */
public interface Perishable {
    public boolean expired(long time);
    public String toJson();
}
