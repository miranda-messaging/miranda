package com.ltsllc.miranda.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateUserOperation extends Operation {
    private User user;

    public User getUser() {
        return user;
    }

    public CreateUserOperation (BlockingQueue<Message> requester, Session session, User user) {
        super("create user operation", requester, session);

        CreateUserOperationReadyState readyState = new CreateUserOperationReadyState( this);
        setCurrentState(readyState);

        this.user = user;
    }

    public void start () {
        super.start();

        Miranda.getInstance().getUserManager().sendCreateUserMessage(getQueue(), this, getUser());
    }

}
