package com.ltsllc.miranda.user;

import com.google.gson.Gson;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.server.HttpPostHandler;
import com.ltsllc.miranda.server.HttpPostMessage;
import io.netty.handler.codec.http.*;

/**
 * Created by Clark on 2/10/2017.
 */
public class NewUserHandler extends HttpPostHandler {
    private UsersFile usersFile;

    public NewUserHandler (UsersFile usersFile) {
        this.usersFile = usersFile;
        setCurrentState(StartState.getInstance());
    }

    public UsersFile getUsersFile() {
        return usersFile;
    }
}
