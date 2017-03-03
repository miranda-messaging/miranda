package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/1/2017.
 */
public class CloseMessage extends Message {
    private int handle;

    public int getHandle() {
        return handle;
    }

    public CloseMessage(BlockingQueue<Message> senderQueue, Object sender, int handle) {
        super(Subjects.Disconnect, senderQueue, sender);

        this.handle = handle;
    }
}
