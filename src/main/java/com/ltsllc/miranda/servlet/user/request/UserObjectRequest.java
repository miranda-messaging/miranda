package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.Request;

public class UserObjectRequest extends Request {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserObjectRequest (String sessionId, User user) {
        super(sessionId);
        setUser(user);
    }
}
