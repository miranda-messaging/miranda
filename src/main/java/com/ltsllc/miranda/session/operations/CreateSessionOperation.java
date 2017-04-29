package com.ltsllc.miranda.session.operations;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/18/2017.
 */
public class CreateSessionOperation extends Operation {
    private String user;

    public String getUser() {
        return user;
    }

    public CreateSessionOperation (BlockingQueue<Message> requester, Session session, String user) {
        super("create session operation", requester, session);

        CreateSessionOperationReadyState readyState = new CreateSessionOperationReadyState(this);
        setCurrentState(readyState);

        this.user = user;
    }

    public void start () {
        super.start();

        Miranda.getInstance().getUserManager().sendGetUser(getQueue(), this, getUser());
    }
}
