package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/10/2017.
 */
public class UpdateUserMessage extends Message {
    private User user;

    public User getUser() {
        return user;
    }

    public UpdateUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        super(Subjects.UpdateUser, senderQueue, sender);

        this.user = user;
    }
}
