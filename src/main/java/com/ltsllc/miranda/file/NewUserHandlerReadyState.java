package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.User;
import com.ltsllc.miranda.server.NewObjectHandlerReadyState;
import com.ltsllc.miranda.user.NewUserHandler;
import com.ltsllc.miranda.user.UsersFile;

import java.lang.reflect.Type;

/**
 * Created by Clark on 2/18/2017.
 */
public class NewUserHandlerReadyState extends NewObjectHandlerReadyState<UsersFile, User, NewUserHandler> {
    @Override
    public Type getBasicType() {
        return User.class;
    }

    public NewUserHandlerReadyState (Consumer consumer, UsersFile usersFile, NewUserHandler newUserHandler) {
        super(consumer, usersFile, newUserHandler);
    }
}
