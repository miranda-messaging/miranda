package com.ltsllc.miranda.servlet.user.request;

import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.requests.Request;

/**
 * A request to update a user
 */
public class UpdateUserRequest extends Request {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UpdateUserRequest (String sessionId) {
        super(sessionId);
    }

}
