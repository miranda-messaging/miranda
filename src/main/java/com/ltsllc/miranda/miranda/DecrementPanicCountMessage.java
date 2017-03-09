package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/8/2017.
 */
public class DecrementPanicCountMessage extends Message {
    public DecrementPanicCountMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.DecrementPanicCount, senderQueue, sender);
    }
}
