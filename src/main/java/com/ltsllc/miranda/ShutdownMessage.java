package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/21/2017.
 */
public class ShutdownMessage extends Message {
    public ShutdownMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.Shutdown, senderQueue, sender);
    }
}
