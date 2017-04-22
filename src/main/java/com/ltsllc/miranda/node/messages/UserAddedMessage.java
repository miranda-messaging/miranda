package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * A remote node added a user
 */
public class UserAddedMessage extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public UserAddedMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.UserAdded, senderQueue, sender);

        this.user = user;
    }
}
