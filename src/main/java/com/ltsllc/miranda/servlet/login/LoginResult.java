package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.user.User;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginResult {
    private User user;
    private String session;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
