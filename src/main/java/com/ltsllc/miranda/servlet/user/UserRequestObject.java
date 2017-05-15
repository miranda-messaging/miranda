package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.servlet.objects.RequestObject;

/**
 * Created by Clark on 4/28/2017.
 */
public class UserRequestObject extends RequestObject {
    private UserObject userObject;

    public UserObject getUserObject() {
        return userObject;
    }

    public void setUserObject(UserObject userObject) {
        this.userObject = userObject;
    }
}
