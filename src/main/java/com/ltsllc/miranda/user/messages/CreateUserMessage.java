package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/11/2017.
 */
public class CreateUserMessage extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public CreateUserMessage(BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.CreateUser, senderQueue, sender);

        this.user = user;
    }
}
