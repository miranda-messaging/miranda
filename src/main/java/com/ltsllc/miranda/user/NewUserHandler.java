package com.ltsllc.miranda.user;

import com.ltsllc.miranda.server.NewObjectPostHandler;

/**
 * Created by Clark on 2/10/2017.
 */
public class NewUserHandler extends NewObjectPostHandler<UsersFile> {

    public NewUserHandler (UsersFile usersFile) {
        super(usersFile);

        NewUserHandlerReadyState readyState = new NewUserHandlerReadyState(this, usersFile, this);
        setCurrentState(readyState);
    }
}
