package com.ltsllc.miranda.servlet.user;

import com.ltsllc.miranda.clientinterface.basicclasses.User;

/**
 * A request to update a user
 */
public class UpdateUserRequest {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
