package com.ltsllc.miranda.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.Operation;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class UpdateUserOperation extends Operation {
    private User user;

    public User getUser() {
        return user;
    }

    public UpdateUserOperation (User user, BlockingQueue<Message> requester) {
        super("update user operation", requester);

        UpdateUserOperationReadyState readyState = new UpdateUserOperationReadyState(this);
        setCurrentState(readyState);

        this.user = user;
    }

    public void start () {
        Miranda.getInstance().getUserManager().sendUpdateUserMessage(getQueue(), this, getUser());
    }
}
