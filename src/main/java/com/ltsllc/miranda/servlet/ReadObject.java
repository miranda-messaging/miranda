package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.objects.ResultObject;

/**
 * Created by Clark on 6/7/2017.
 */
public class ReadObject extends ResultObject {
    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
