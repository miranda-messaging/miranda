package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/13/2017.
 */
public class UserUpdatedMessage extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public UserUpdatedMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.UserUpdated, senderQueue, sender);

        this.user = user;
    }
}
