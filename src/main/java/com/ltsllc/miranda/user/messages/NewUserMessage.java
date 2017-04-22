package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class NewUserMessage extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public NewUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.NewUser, senderQueue, sender);

        this.user = user;
    }
}
