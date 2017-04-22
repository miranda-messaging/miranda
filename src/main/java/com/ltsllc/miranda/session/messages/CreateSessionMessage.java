package com.ltsllc.miranda.session.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class CreateSessionMessage extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public CreateSessionMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.CreateSession, senderQueue, sender);

        this.user = user;
    }
}
