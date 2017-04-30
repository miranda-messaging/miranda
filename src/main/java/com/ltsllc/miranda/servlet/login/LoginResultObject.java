package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.servlet.objects.ResultObject;

/**
 * Created by Clark on 4/15/2017.
 */
public class LoginResultObject extends ResultObject {
    private String session;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
