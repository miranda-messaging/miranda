package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/10/2017.
 */
public class UpdateUserMessage extends Message {
    private Session session;
    private User user;

    public User getUser() {
        return user;
    }

    public Session getSession() {
        return session;
    }

    public UpdateUserMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, User user) {
        super(Subjects.UpdateUser, senderQueue, sender);

        this.session = session;
        this.user = user;
    }
}
