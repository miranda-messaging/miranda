package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.SessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/11/2017.
 */
public class CreateUserMessage extends SessionMessage {
    private User user;

    public User getUser() {
        return user;
    }

    public CreateUserMessage(BlockingQueue<Message> senderQueue, Object sender, Session session, User user) {
        super(Subjects.CreateUser, senderQueue, sender, session);

        this.user = user;
    }
}
