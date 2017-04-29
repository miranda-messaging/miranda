package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.SessionMessage;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/10/2017.
 */
public class DeleteUserMessage extends SessionMessage {
    private String name;

    public String getName() {
        return name;
    }

    public DeleteUserMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, String name) {
        super(Subjects.DeleteUser, senderQueue, sender, session);

        this.name = name;
    }
}
