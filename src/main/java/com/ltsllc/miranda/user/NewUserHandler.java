package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.file.NewUserHandlerReadyState;
import com.ltsllc.miranda.server.HttpPostHandler;
import com.ltsllc.miranda.server.HttpPostMessage;
import com.ltsllc.miranda.server.NewObjectHandlerReadyState;
import com.ltsllc.miranda.server.NewObjectPostHandler;
import io.netty.handler.codec.http.*;

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
