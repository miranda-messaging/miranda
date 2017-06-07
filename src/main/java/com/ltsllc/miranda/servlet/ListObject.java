package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.objects.ResultObject;

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
