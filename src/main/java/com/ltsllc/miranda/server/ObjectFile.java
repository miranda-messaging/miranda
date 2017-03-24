package com.ltsllc.miranda.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/18/2017.
 */
public class ObjectFile<T> {
    private List<T> data = new ArrayList<T>();

    public List<T> getData() {
        return data;
    }

    public boolean contains (T t) {
        for (T instance : getData()) {
            if (instance.equals(t))
                return true;
        }

        return false;
    }

    public void add (T t) {
        getData().add(t);
    }
}
