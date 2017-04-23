package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class GetSubscriptionMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public GetSubscriptionMessage(BlockingQueue<Message> sender, Object senderObject, String name) {
        super(Subjects.GetSubcription, sender, senderObject);
        this.name = name;
    }
}
