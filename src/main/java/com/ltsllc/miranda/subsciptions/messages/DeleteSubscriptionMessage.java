package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.SessionMessage;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionMessage extends SessionMessage {
    private String name;

    public String getName() {
        return name;
    }

    public DeleteSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, String name) {
        super(Subjects.DeleteSubscription, senderQueue, sender, session);

        this.name = name;
    }
}
