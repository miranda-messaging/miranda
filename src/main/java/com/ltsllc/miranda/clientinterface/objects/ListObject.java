package com.ltsllc.miranda.clientinterface.objects;

import com.ltsllc.miranda.clientinterface.results.ResultObject;

import java.util.List;

/**
 * Created by Clark on 6/7/2017.
 */
public class ListObject extends ResultObject {
    private List list;

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
