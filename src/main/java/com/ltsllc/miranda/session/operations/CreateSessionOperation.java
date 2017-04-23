package com.ltsllc.miranda.session.operations;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/18/2017.
 */
public class CreateSessionOperation extends Operation {
    private String user;

    public String getUser() {
        return user;
    }

    public CreateSessionOperation (BlockingQueue<Message> requester, String user) {
        super("create session operation", requester);

        CreateSessionOperationReadyState readyState = new CreateSessionOperationReadyState(this);
        setCurrentState(readyState);

        this.user = user;
    }

    public void start () {
        super.start();

        Miranda.getInstance().getUserManager().sendGetUser(getQueue(), this, getUser());
    }
}
