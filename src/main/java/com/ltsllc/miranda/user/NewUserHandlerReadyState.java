package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.server.NewObjectHandlerReadyState;

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

    public State processMessage (Message message) {
        return super.processMessage(message);
    }
}
