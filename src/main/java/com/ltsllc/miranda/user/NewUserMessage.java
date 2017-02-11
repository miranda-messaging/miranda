package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class NewUserMessage extends Message {
    private User user;

    public NewUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.NewUser, senderQueue, sender);

        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
