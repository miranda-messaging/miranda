package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/31/2017.
 */
public class RemoveSubscriberMessage extends Message {
    public RemoveSubscriberMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super (Subjects.RemoveSubscriber, senderQueue, sender);
    }
}
