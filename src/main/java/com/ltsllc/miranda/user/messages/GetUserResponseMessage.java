package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/2/2017.
 */
public class GetUserResponseMessage extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public GetUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.GetUserResponse, senderQueue, sender);

        this.user = user;
    }
}
