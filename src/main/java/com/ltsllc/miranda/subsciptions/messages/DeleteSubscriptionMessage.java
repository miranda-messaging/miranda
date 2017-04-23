package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public DeleteSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.DeleteSubscription, senderQueue, sender);

        this.name = name;
    }
}
