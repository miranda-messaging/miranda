package com.ltsllc.miranda.clientinterface.objects;

import com.ltsllc.miranda.clientinterface.results.ResultObject;

/**
 * Created by Clark on 6/7/2017.
 */
public class ReadObject<T> extends ResultObject {
    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
