package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/12/2017.
 */
public class EvictMessage extends Message {
    public EvictMessage(BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.Evict, senderQueue, senderObject);
    }
}
