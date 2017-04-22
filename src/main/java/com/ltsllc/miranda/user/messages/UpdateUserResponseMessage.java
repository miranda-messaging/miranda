package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/11/2017.
 */
public class UpdateUserResponseMessage extends Message {
    private User user;
    private Results result;

    public User getUser () {
        return user;
    }

    public Results getResult() {
        return result;
    }

    public UpdateUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, User user, Results result) {
        super(Subjects.UpdateUserResponse, senderQueue, sender);

        this.user = user;
        this.result = result;
    }
}
