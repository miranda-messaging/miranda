package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.user.User;

/**
 * Created by Clark on 4/15/2017.
 */
public class UserObjectResultObject extends ResultObject {
    private UserObject userObject;

    public UserObject getUserObject() {
        return userObject;
    }

    public void setUserObject(UserObject userObject) {
        this.userObject = userObject;
    }
}
