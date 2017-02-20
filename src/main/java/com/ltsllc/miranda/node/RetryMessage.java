package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */
public class RetryMessage extends Message {
    public RetryMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.Retry, senderQueue, sender);
    }
}
