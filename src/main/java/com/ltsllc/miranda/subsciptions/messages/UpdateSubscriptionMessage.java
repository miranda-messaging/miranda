package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.SessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class UpdateSubscriptionMessage extends SessionMessage {
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public UpdateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                      Subscription subscription) {
        super(Message.Subjects.UpdateSubscription, senderQueue, sender, session);

        this.subscription = subscription;
    }
}
