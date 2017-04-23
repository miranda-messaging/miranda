package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class GetSubscriptionsMessage extends Message {
    public GetSubscriptionsMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Message.Subjects.GetSubscriptions, senderQueue, sender);
    }
}
