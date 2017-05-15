package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.user.User;

import java.util.List;

/**
 * Created by Clark on 4/15/2017.
 */
public class UserListResultObject extends ResultObject {
    private List<User> userList;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
