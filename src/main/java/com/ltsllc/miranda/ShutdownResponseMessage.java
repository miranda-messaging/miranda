package com.ltsllc.miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/21/2017.
 */
public class ShutdownResponseMessage extends Message {
    public ShutdownResponseMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.ShutdownResponse, senderQueue, sender);
    }
}
