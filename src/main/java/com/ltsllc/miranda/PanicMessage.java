package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/2/2017.
 */
public class PanicMessage extends Message {
    public PanicMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.Panic, senderQueue, sender);
    }
}
