package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/2/2017.
 */
public class ClosedMessage extends Message {
    private int handle;

    public int getHandle() {
        return handle;
    }

    public ClosedMessage (BlockingQueue<Message> senderQueue, Object sender, int handle) {
        super(Subjects.Closed, senderQueue, sender);

        this.handle = handle;
    }
}
