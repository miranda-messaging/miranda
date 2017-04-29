package com.ltsllc.miranda.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class LoginOperation extends Operation {
    private String user;

    public String getUser() {
        return user;
    }

    public LoginOperation (String name, BlockingQueue<Message> requester) {
        super ("login operation", requester, null);

        this.user = name;

        LoginOperationReadyState readyState = new LoginOperationReadyState(this);
        setCurrentState(readyState);
    }

    public void start () {
        super.start();

        Miranda.getInstance().getSessionManager().sendGetSessionMessage(getQueue(), this, getUser());
    }
}
